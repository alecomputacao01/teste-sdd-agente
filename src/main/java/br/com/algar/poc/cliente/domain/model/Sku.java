package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object — código de identificação comercial do produto.
 * Regra: 3 a 4 letras maiúsculas, hífen, 4 a 6 dígitos (ex.: {@code ABC-1234}).
 */
public final class Sku {

    private static final Pattern FORMAT = Pattern.compile("^[A-Z]{3,4}-\\d{4,6}$");

    private final String value;

    private Sku(String value) {
        this.value = value;
    }

    public static Sku of(String value) {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("SKU inválido: deve seguir o formato ABC-1234 (letras-dígitos)");
        }
        return new Sku(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
