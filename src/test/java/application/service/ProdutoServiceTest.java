package application.service;

import application.port.ProdutoRepositoryPort;
import domain.model.Produto;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoServiceTest {
    private InMemoryProdutoRepository repository;
    private ProdutoService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProdutoRepository();
        service = new ProdutoService(repository);
    }

    @Test
    void registraEListaProduto() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", "Café torrado"));

        assertEquals(1L, registrado.getId());
        assertEquals("Café", service.listarProdutos().getFirst().getNome());
    }

    @Test
    void buscaProdutoPorNome() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", "Café torrado"));

        assertEquals(registrado, service.buscarProdutoPorNome("Café"));
    }

    @Test
    void rejeitaNomeDeBuscaVazio() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.buscarProdutoPorNome(" ")
        );

        assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    void retornaNaoEncontradoParaNomeInexistente() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.buscarProdutoPorNome("Inexistente")
        );

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void rejeitaCadastroNulo() {
        assertThrows(BadRequestException.class, () -> service.registrarProduto(null));
    }

    @Test
    void atualizaProdutoCompleto() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", "Café torrado"));

        Produto atualizado = service.atualizarProdutoPorId(
                registrado.getId(),
                new Produto(null, "Chá", "Bebidas", "Chá verde")
        );

        assertEquals(registrado.getId(), atualizado.getId());
        assertEquals("Chá", atualizado.getNome());
        assertEquals("Bebidas", atualizado.getCategoria());
        assertEquals("Chá verde", atualizado.getDescricao());
    }

    @Test
    void rejeitaAtualizacaoCompletaSemNome() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.atualizarProdutoPorId(1L, new Produto(null, null, "Bebidas", null))
        );

        assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    void retornaNaoEncontradoAoAtualizarProdutoInexistente() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.atualizarProdutoPorId(99L, new Produto(null, "Café", "Bebidas", null))
        );

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    void atualizaProdutoParcialmente() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", "Café torrado"));

        Produto atualizado = service.atualizarParcialmenteProduto(
                registrado.getId(),
                new Produto(null, "Café especial", null, null)
        );

        assertEquals("Café especial", atualizado.getNome());
        assertEquals("Bebidas", atualizado.getCategoria());
        assertEquals("Café torrado", atualizado.getDescricao());
    }

    @Test
    void rejeitaAtualizacaoParcialNula() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.atualizarParcialmenteProduto(1L, null)
        );

        assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    void rejeitaAtualizacaoParcialQueDeixaNomeEmBranco() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", null));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.atualizarParcialmenteProduto(
                        registrado.getId(),
                        new Produto(null, " ", null, null)
                )
        );

        assertEquals(400, exception.getResponse().getStatus());
    }

    @Test
    void excluiProdutoExistente() {
        Produto registrado = service.registrarProduto(new Produto(null, "Café", "Bebidas", "Café torrado"));

        service.deletarProdutoPorId(registrado.getId());

        assertEquals(0, service.listarProdutos().size());
        assertThrows(NotFoundException.class, () -> service.buscarProdutoPorId(registrado.getId()));
    }

    @Test
    void naoExcluiProdutoInexistente() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.deletarProdutoPorId(99L)
        );

        assertEquals(404, exception.getResponse().getStatus());
        assertNull(repository.buscarProdutoPorId(99L).orElse(null));
    }

    @Test
    void rejeitaProdutoSemNome() {
        assertThrows(
                BadRequestException.class,
                () -> service.registrarProduto(new Produto(null, " ", "Bebidas", null))
        );
    }

    @Test
    void retornaNaoEncontradoParaIdInexistente() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.buscarProdutoPorId(99L)
        );

        assertEquals(404, exception.getResponse().getStatus());
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
