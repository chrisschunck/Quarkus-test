package domain.mapper;

import adapters.out.persistence.entity.ProdutoJpaEntity;
import domain.model.Produto;

public class ProdutoMapper {
    private ProdutoMapper() {
    }

    public static ProdutoJpaEntity toEntity(Produto produto) {
        ProdutoJpaEntity entity = new ProdutoJpaEntity();
        entity.setId(produto.getId());
        entity.setNome(produto.getNome());
        entity.setCategoria(produto.getCategoria());
        entity.setDescricao(produto.getDescricao());
        return entity;
    }

    public static Produto toDomain(ProdutoJpaEntity entity) {
        return new Produto(entity.getId(), entity.getNome(), entity.getCategoria(), entity.getDescricao());
    }
}
