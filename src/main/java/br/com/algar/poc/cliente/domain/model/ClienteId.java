package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Value Object — identidade do Aggregate {@link Cliente}. */
public final class ClienteId {

    private final UUID value;

    private ClienteId(UUID value) {
        this.value = value;
    }

    public static ClienteId novo() {
        return new ClienteId(UUID.randomUUID());
    }

    public static ClienteId de(UUID value) {
        Objects.requireNonNull(value, "id do cliente não pode ser nulo");
        return new ClienteId(value);
    }

    public static ClienteId de(String value) {
        return de(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClienteId other)) return false;
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
