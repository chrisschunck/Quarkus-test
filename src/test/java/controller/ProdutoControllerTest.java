package controller;

import adapters.in.web.controller.ProdutoController;
import adapters.in.web.dto.ProdutoRequestDTO;
import adapters.in.web.dto.ProdutoResponseDTO;
import application.port.ProdutoRepositoryPort;
import application.service.ProdutoService;
import domain.model.Produto;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProdutoControllerTest {
    private ProdutoController controller;

    @BeforeEach
    void setUp() {
        controller = new ProdutoController(new ProdutoService(new InMemoryProdutoRepository()));
    }

    @Test
    void listaProdutos() {
        cadastrarProduto();

        List<ProdutoResponseDTO> produtos = controller.listarProdutos();

        assertEquals(1, produtos.size());
        assertEquals("Café", produtos.getFirst().nome());
        assertEquals("Bebidas", produtos.getFirst().categoria());
    }

    @Test
    void buscaProdutoPorId() {
        ProdutoResponseDTO cadastrado = cadastrarProduto();

        ProdutoResponseDTO encontrado = controller.buscarProdutoPorId(cadastrado.id());

        assertEquals(cadastrado, encontrado);
    }

    @Test
    void buscaProdutoPorNome() {
        ProdutoResponseDTO cadastrado = cadastrarProduto();

        ProdutoResponseDTO encontrado = controller.buscarProdutoPorNome("Café");

        assertEquals(cadastrado, encontrado);
    }

    @Test
    void registraProdutoComStatusCreated() {
        Response response = controller.registrarProduto(
                new ProdutoRequestDTO("Café", "Bebidas", "Café torrado")
        );

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals("Café", ((ProdutoResponseDTO) response.getEntity()).nome());
        response.close();
    }

    @Test
    void atualizaProduto() {
        ProdutoResponseDTO cadastrado = cadastrarProduto();

        ProdutoResponseDTO atualizado = controller.atualizarProdutoPorId(
                cadastrado.id(),
                new ProdutoRequestDTO("Café especial", "Bebidas", "Café moído")
        );

        assertEquals(cadastrado.id(), atualizado.id());
        assertEquals("Café especial", atualizado.nome());
        assertEquals("Café moído", atualizado.descricao());
    }

    @Test
    void atualizaProdutoParcialmente() {
        ProdutoResponseDTO cadastrado = cadastrarProduto();

        ProdutoResponseDTO atualizado = controller.atualizarParcialmenteProduto(
                cadastrado.id(),
                new ProdutoRequestDTO("Café especial", null, null)
        );

        assertEquals("Café especial", atualizado.nome());
        assertEquals("Bebidas", atualizado.categoria());
        assertEquals("Café torrado", atualizado.descricao());
    }

    @Test
    void excluiProdutoSemConteudoNaResposta() {
        ProdutoResponseDTO cadastrado = cadastrarProduto();

        Response response = controller.deletarProdutoPorId(cadastrado.id());

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        assertEquals(0, controller.listarProdutos().size());
        response.close();
    }

    private ProdutoResponseDTO cadastrarProduto() {
        Response response = controller.registrarProduto(
                new ProdutoRequestDTO("Café", "Bebidas", "Café torrado")
        );
        ProdutoResponseDTO produto = (ProdutoResponseDTO) response.getEntity();
        response.close();
        return produto;
    }

    private static class InMemoryProdutoRepository implements ProdutoRepositoryPort {
        private final List<Produto> produtos = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Produto registrar(Produto produto) {
            produto.setId(nextId++);
            produtos.add(produto);
            return produto;
        }

        @Override
        public List<Produto> listarProdutos() {
            return List.copyOf(produtos);
        }

        @Override
        public Optional<Produto> buscarProdutoPorId(Long id) {
            return produtos.stream().filter(produto -> produto.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Produto> buscarProdutoPorNome(String nome) {
            return produtos.stream().filter(produto -> produto.getNome().equals(nome)).findFirst();
        }

        @Override
        public Produto atualizarProduto(Produto produto) {
            return produto;
        }

        @Override
        public void deletarProdutoPorId(Long id) {
            produtos.removeIf(produto -> produto.getId().equals(id));
        }
    }
}
