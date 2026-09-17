package br.com.algar.poc.cliente.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ContratoEmbeddable {

    @Column(name = "contrato_id", nullable = false)
    private String contratoId;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String status;

    protected ContratoEmbeddable() {
        // exigido pelo JPA
    }

    public ContratoEmbeddable(String contratoId, String descricao, String status) {
        this.contratoId = contratoId;
        this.descricao = descricao;
        this.status = status;
    }

    public String getContratoId() {
        return contratoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }
}
