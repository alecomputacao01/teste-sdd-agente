package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Value Object — identidade da entidade filha {@link ContaFaturamento}, dentro do agregado Cliente. */
public final class ContaFaturamentoId {

    private final UUID value;

    private ContaFaturamentoId(UUID value) {
        this.value = value;
    }

    public static ContaFaturamentoId novo() {
        return new ContaFaturamentoId(UUID.randomUUID());
    }

    public static ContaFaturamentoId de(UUID value) {
        Objects.requireNonNull(value, "id da conta de faturamento não pode ser nulo");
        return new ContaFaturamentoId(value);
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaFaturamentoId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
