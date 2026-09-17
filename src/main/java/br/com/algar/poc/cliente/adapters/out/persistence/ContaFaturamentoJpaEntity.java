package br.com.algar.poc.cliente.adapters.out.persistence;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "conta_faturamento")
public class ContaFaturamentoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "logradouro", column = @Column(name = "endereco_logradouro", nullable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "endereco_numero", nullable = false)),
            @AttributeOverride(name = "cep", column = @Column(name = "endereco_cep", nullable = false)),
            @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade", nullable = false)),
            @AttributeOverride(name = "uf", column = @Column(name = "endereco_uf", nullable = false)),
            @AttributeOverride(name = "principal", column = @Column(name = "endereco_principal", nullable = false)),
            @AttributeOverride(name = "validadoNosCorreios", column = @Column(name = "endereco_validado_nos_correios", nullable = false))
    })
    private EnderecoEmbeddable enderecoDeCobranca;

    protected ContaFaturamentoJpaEntity() {
        // exigido pelo JPA
    }

    public ContaFaturamentoJpaEntity(UUID id, String descricao, EnderecoEmbeddable enderecoDeCobranca) {
        this.id = id;
        this.descricao = descricao;
        this.enderecoDeCobranca = enderecoDeCobranca;
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public EnderecoEmbeddable getEnderecoDeCobranca() {
        return enderecoDeCobranca;
    }
}
