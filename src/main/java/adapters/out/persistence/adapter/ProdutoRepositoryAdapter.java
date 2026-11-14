package adapters.out.persistence.adapter;

import adapters.out.persistence.entity.ProdutoJpaEntity;
import adapters.out.persistence.repository.ProdutoJpaRepository;
import application.port.ProdutoRepositoryPort;
import domain.mapper.ProdutoMapper;
import domain.model.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepositoryAdapter implements ProdutoRepositoryPort {
    private final ProdutoJpaRepository repository;

    public ProdutoRepositoryAdapter(ProdutoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Produto registrar(Produto produto) {
        ProdutoJpaEntity entity = ProdutoMapper.toEntity(produto);
        repository.persist(entity);
        return ProdutoMapper.toDomain(entity);
    }

    @Override
    public List<Produto> listarProdutos() {
        return repository.listAll().stream()
                .map(ProdutoMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Produto> buscarProdutoPorId(Long id) {
        return repository.findByIdOptional(id).map(ProdutoMapper::toDomain);
    }

    @Override
    public Optional<Produto> buscarProdutoPorNome(String nome) {
        return repository.find("nome", nome).firstResultOptional()
                .map(ProdutoMapper::toDomain);
    }

    @Override
    @Transactional
    public Produto atualizarProduto(Produto produto) {
        ProdutoJpaEntity entity = repository.findByIdOptional(produto.getId())
                .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + produto.getId()));
        entity.setNome(produto.getNome());
        entity.setCategoria(produto.getCategoria());
        entity.setDescricao(produto.getDescricao());
        return ProdutoMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void deletarProdutoPorId(Long id) {
        repository.deleteById(id);
    }
}
