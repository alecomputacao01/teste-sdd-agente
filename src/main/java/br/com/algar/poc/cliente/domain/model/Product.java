package br.com.algar.poc.cliente.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Aggregate Root do bounded context Catálogo. Sem dependência de framework —
 * regra de negócio pura, verificada pelo ArchUnit (ver arch.HexagonalArchitectureTest).
 */
public final class Product {

    private final ProductId id;
    private final Sku sku;
    private String name;
    private BigDecimal price;

    private Product(ProductId id, Sku sku, String name, BigDecimal price) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
    }

    public static Product register(ProductId id, Sku sku, String name, BigDecimal price) {
        Objects.requireNonNull(id, "id não pode ser nulo");
        Objects.requireNonNull(sku, "sku não pode ser nulo");
        requireValidName(name);
        requireValidPrice(price);
        return new Product(id, sku, name.trim(), price);
    }

    public void rename(String newName) {
        requireValidName(newName);
        this.name = newName.trim();
    }

    private static void requireValidName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("nome do produto não pode ser vazio");
        }
    }

    private static void requireValidPrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("preço do produto deve ser maior que zero");
        }
    }

    public ProductId id() {
        return id;
    }

    public Sku sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }
}
