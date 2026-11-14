package adapters.out.persistence.repository;

import adapters.out.persistence.entity.ProdutoJpaEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutoJpaRepository implements PanacheRepository<ProdutoJpaEntity> {
}
