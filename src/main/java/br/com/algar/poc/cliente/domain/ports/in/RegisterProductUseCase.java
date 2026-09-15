package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.Product;

import java.math.BigDecimal;

/** Porta de entrada (driving port) — único ponto de entrada para registrar um produto. */
public interface RegisterProductUseCase {

    Product register(String sku, String name, BigDecimal price);
}
