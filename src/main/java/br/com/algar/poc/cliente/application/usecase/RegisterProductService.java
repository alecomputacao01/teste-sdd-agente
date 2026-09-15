package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Product;
import br.com.algar.poc.cliente.domain.model.ProductId;
import br.com.algar.poc.cliente.domain.model.Sku;
import br.com.algar.poc.cliente.domain.ports.in.RegisterProductUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ProductRepository;

import java.math.BigDecimal;

/**
 * Implementa a porta de entrada, orquestrando domínio e porta de saída — não depende de nenhum
 * adapter concreto (regra fiscalizada por HexagonalArchitectureTest.application_nao_depende_de_adapters).
 */
public class RegisterProductService implements RegisterProductUseCase {

    private final ProductRepository repository;

    public RegisterProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product register(String skuValue, String name, BigDecimal price) {
        var sku = Sku.of(skuValue);
        if (repository.existsBySku(sku)) {
            throw new IllegalStateException("SKU já cadastrado: " + skuValue);
        }
        var product = Product.register(ProductId.newId(), sku, name, price);
        return repository.save(product);
    }
}
