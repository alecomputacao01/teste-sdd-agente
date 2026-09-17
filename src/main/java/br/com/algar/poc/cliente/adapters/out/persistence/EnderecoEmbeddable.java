package br.com.algar.poc.cliente.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EnderecoEmbeddable {

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false)
    private boolean principal;

    @Column(name = "validado_nos_correios", nullable = false)
    private boolean validadoNosCorreios;

    protected EnderecoEmbeddable() {
        // exigido pelo JPA
    }

    public EnderecoEmbeddable(String logradouro, String numero, String cep, String cidade, String uf,
                               boolean principal, boolean validadoNosCorreios) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
        this.principal = principal;
        this.validadoNosCorreios = validadoNosCorreios;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public boolean isValidadoNosCorreios() {
        return validadoNosCorreios;
    }
}
