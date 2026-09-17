package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;

/**
 * Value Object — dados de contato do cliente. Imutável: qualquer alteração gera uma nova instância
 * validada (spec.md RF-009).
 */
public final class DadosContato {

    private final String telefone;
    private final String email;

    private DadosContato(String telefone, String email) {
        this.telefone = telefone;
        this.email = email;
    }

    public static DadosContato de(String telefone, String email) {
        requireNaoBranco(telefone, "telefone");
        requireNaoBranco(email, "e-mail");
        return new DadosContato(telefone.trim(), email.trim());
    }

    public DadosContato comTelefone(String novoTelefone) {
        return DadosContato.de(novoTelefone, email);
    }

    public DadosContato comEmail(String novoEmail) {
        return DadosContato.de(telefone, novoEmail);
    }

    private static void requireNaoBranco(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
    }

    public String telefone() {
        return telefone;
    }

    public String email() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DadosContato other)) return false;
        return telefone.equals(other.telefone) && email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(telefone, email);
    }

    @Override
    public String toString() {
        return telefone + " / " + email;
    }
}
