package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;

/**
 * Entity filha do agregado {@link Cliente} — uma conta de faturamento (ex.: banda larga cobrada em
 * um endereço, celular em outro). Tem identidade própria, mas só existe e é persistida através do
 * Cliente (spec.md RF-008); não tem repositório próprio.
 */
public final class ContaFaturamento {

    private final ContaFaturamentoId id;
    private final String descricao;
    private final Endereco enderecoDeCobranca;

    private ContaFaturamento(ContaFaturamentoId id, String descricao, Endereco enderecoDeCobranca) {
        this.id = id;
        this.descricao = descricao;
        this.enderecoDeCobranca = enderecoDeCobranca;
    }

    public static ContaFaturamento criar(ContaFaturamentoId id, String descricao, Endereco enderecoDeCobranca) {
        Objects.requireNonNull(id, "id da conta de faturamento não pode ser nulo");
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descrição da conta de faturamento não pode ser vazia");
        }
        Objects.requireNonNull(enderecoDeCobranca, "endereço de cobrança não pode ser nulo");
        return new ContaFaturamento(id, descricao.trim(), enderecoDeCobranca);
    }

    public ContaFaturamentoId id() {
        return id;
    }

    public String descricao() {
        return descricao;
    }

    public Endereco enderecoDeCobranca() {
        return enderecoDeCobranca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaFaturamento other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
