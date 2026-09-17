package br.com.algar.poc.cliente.domain.model;

import java.util.Objects;

/**
 * Value Object — endereço de instalação. Imutável: qualquer alteração gera uma nova instância
 * validada (spec.md RF-009). Sem identidade própria fora do {@link Cliente}.
 *
 * {@code validadoNosCorreios} chega já resolvido por quem constrói o endereço (plan.md §0.1 — sem
 * porta de saída dedicada para consultar os Correios nesta PoC); é usado pela fábrica de
 * {@link Cliente} para decidir a ativação do cadastro (spec.md RF-010).
 */
public final class Endereco {

    private final String logradouro;
    private final String numero;
    private final String cep;
    private final String cidade;
    private final String uf;
    private final boolean principal;
    private final boolean validadoNosCorreios;

    private Endereco(String logradouro, String numero, String cep, String cidade, String uf,
                      boolean principal, boolean validadoNosCorreios) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
        this.principal = principal;
        this.validadoNosCorreios = validadoNosCorreios;
    }

    public static Endereco de(String logradouro, String numero, String cep, String cidade, String uf,
                               boolean principal, boolean validadoNosCorreios) {
        requireNaoBranco(logradouro, "logradouro");
        requireNaoBranco(numero, "número");
        requireNaoBranco(cep, "CEP");
        requireNaoBranco(cidade, "cidade");
        requireNaoBranco(uf, "UF");
        return new Endereco(logradouro.trim(), numero.trim(), cep.trim(), cidade.trim(), uf.trim(),
                principal, validadoNosCorreios);
    }

    public Endereco comoPrincipal() {
        return new Endereco(logradouro, numero, cep, cidade, uf, true, validadoNosCorreios);
    }

    private static void requireNaoBranco(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " do endereço não pode ser vazio");
        }
    }

    public String logradouro() {
        return logradouro;
    }

    public String numero() {
        return numero;
    }

    public String cep() {
        return cep;
    }

    public String cidade() {
        return cidade;
    }

    public String uf() {
        return uf;
    }

    public boolean principal() {
        return principal;
    }

    public boolean validadoNosCorreios() {
        return validadoNosCorreios;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco other)) return false;
        return principal == other.principal
                && validadoNosCorreios == other.validadoNosCorreios
                && logradouro.equals(other.logradouro)
                && numero.equals(other.numero)
                && cep.equals(other.cep)
                && cidade.equals(other.cidade)
                && uf.equals(other.uf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, cep, cidade, uf, principal, validadoNosCorreios);
    }

    @Override
    public String toString() {
        return logradouro + ", " + numero + " - " + cidade + "/" + uf + " " + cep;
    }
}
