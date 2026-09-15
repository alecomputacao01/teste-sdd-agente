package br.com.algar.poc.cliente.domain.ports.out;

import br.com.algar.poc.cliente.domain.model.Product;
import br.com.algar.poc.cliente.domain.model.ProductId;
import br.com.algar.poc.cliente.domain.model.Sku;

import java.util.Optional;

/** Porta de saída (driven port) — persistência do Aggregate, sem detalhe de infraestrutura. */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(ProductId id);

    boolean existsBySku(Sku sku);
}
