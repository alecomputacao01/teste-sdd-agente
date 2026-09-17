package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;

/**
 * Value Object — CPF/CNPJ unificado, identidade única do {@link Cliente} (spec.md RF-008).
 * Validação de formato básica: apenas 11 (CPF) ou 14 (CNPJ) dígitos após remover máscara.
 */
public final class Documento {

    private final String value;

    private Documento(String value) {
        this.value = value;
    }

    public static Documento de(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("documento não pode ser nulo");
        }
        var digitos = valor.replaceAll("\\D", "");
        if (digitos.length() != 11 && digitos.length() != 14) {
            throw new IllegalArgumentException("documento inválido: deve ter 11 (CPF) ou 14 (CNPJ) dígitos");
        }
        return new Documento(digitos);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Documento other)) return false;
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
