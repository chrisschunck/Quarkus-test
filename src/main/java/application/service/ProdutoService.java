package application.service;

import application.port.ProdutoRepositoryPort;
import domain.model.Produto;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class ProdutoService {
    private final ProdutoRepositoryPort produtoRepositoryPort;

    public ProdutoService(ProdutoRepositoryPort produtoRepositoryPort) {
        this.produtoRepositoryPort = produtoRepositoryPort;
    }

    public List<Produto> listarProdutos() {
        return produtoRepositoryPort.listarProdutos();
    }

    public Produto buscarProdutoPorId(Long id) {
        return produtoRepositoryPort.buscarProdutoPorId(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + id));
    }

    public Produto buscarProdutoPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("O nome do produto é obrigatório");
        }
        return produtoRepositoryPort.buscarProdutoPorNome(nome)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + nome));
    }

    public Produto registrarProduto(Produto produto) {
        validarProduto(produto);
        return produtoRepositoryPort.registrar(produto);
    }

    public Produto atualizarProdutoPorId(Long id, Produto produto) {
        validarProduto(produto);
        buscarProdutoPorId(id);
        produto.setId(id);
        return produtoRepositoryPort.atualizarProduto(produto);
    }

    public Produto atualizarParcialmenteProduto(Long id, Produto atualizacao) {
        if (atualizacao == null) {
            throw new BadRequestException("O corpo da requisição é obrigatório");
        }
        Produto produto = buscarProdutoPorId(id);
        if (atualizacao.getNome() != null) {
            produto.setNome(atualizacao.getNome());
        }
        if (atualizacao.getCategoria() != null) {
            produto.setCategoria(atualizacao.getCategoria());
        }
        if (atualizacao.getDescricao() != null) {
            produto.setDescricao(atualizacao.getDescricao());
        }
        validarProduto(produto);
        return produtoRepositoryPort.atualizarProduto(produto);
    }

    public void deletarProdutoPorId(Long id) {
        buscarProdutoPorId(id);
        produtoRepositoryPort.deletarProdutoPorId(id);
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new BadRequestException("O corpo da requisição é obrigatório");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new BadRequestException("O nome do produto é obrigatório");
        }
    }
}
