package application.port;

import domain.model.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepositoryPort {
    Produto registrar(Produto produto);

    List<Produto> listarProdutos();

    Optional<Produto> buscarProdutoPorId(Long id);

    Optional<Produto> buscarProdutoPorNome(String nome);

    Produto atualizarProduto(Produto produto);

    void deletarProdutoPorId(Long id);
}
