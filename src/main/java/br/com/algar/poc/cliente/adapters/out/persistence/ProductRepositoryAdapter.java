package br.com.algar.poc.cliente.adapters.out.persistence;

import br.com.algar.poc.cliente.domain.model.Product;
import br.com.algar.poc.cliente.domain.model.ProductId;
import br.com.algar.poc.cliente.domain.model.Sku;
import br.com.algar.poc.cliente.domain.ports.out.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        var saved = jpaRepository.save(ProductMapper.toEntity(product));
        return ProductMapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(id.value()).map(ProductMapper::toDomain);
    }

    @Override
    public boolean existsBySku(Sku sku) {
        return jpaRepository.existsBySku(sku.value());
    }
}
