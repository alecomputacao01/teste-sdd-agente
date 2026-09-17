package br.com.algar.poc.cliente.adapters.out.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 14)
    private String documento;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false)
    private String email;

    @Column(name = "score_credito", length = 10)
    private String scoreCredito;

    @Column(name = "possui_contestacao_aberta", nullable = false)
    private boolean possuiContestacaoAbertaUltimos6Meses;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cliente_endereco", joinColumns = @JoinColumn(name = "cliente_id"))
    @OrderColumn(name = "endereco_ordem")
    private List<EnderecoEmbeddable> enderecos = new ArrayList<>();

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private List<ContaFaturamentoJpaEntity> contasFaturamento = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cliente_contrato", joinColumns = @JoinColumn(name = "cliente_id"))
    @OrderColumn(name = "contrato_ordem")
    private List<ContratoEmbeddable> contratos = new ArrayList<>();

    protected ClienteJpaEntity() {
        // exigido pelo JPA
    }

    public ClienteJpaEntity(UUID id, String documento, String status, String telefone, String email,
                             String scoreCredito, boolean possuiContestacaoAbertaUltimos6Meses,
                             List<EnderecoEmbeddable> enderecos, List<ContaFaturamentoJpaEntity> contasFaturamento,
                             List<ContratoEmbeddable> contratos) {
        this.id = id;
        this.documento = documento;
        this.status = status;
        this.telefone = telefone;
        this.email = email;
        this.scoreCredito = scoreCredito;
        this.possuiContestacaoAbertaUltimos6Meses = possuiContestacaoAbertaUltimos6Meses;
        this.enderecos = enderecos;
        this.contasFaturamento = contasFaturamento;
        this.contratos = contratos;
    }

    public UUID getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getStatus() {
        return status;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getScoreCredito() {
        return scoreCredito;
    }

    public boolean isPossuiContestacaoAbertaUltimos6Meses() {
        return possuiContestacaoAbertaUltimos6Meses;
    }

    public List<EnderecoEmbeddable> getEnderecos() {
        return enderecos;
    }

    public List<ContaFaturamentoJpaEntity> getContasFaturamento() {
        return contasFaturamento;
    }

    public List<ContratoEmbeddable> getContratos() {
        return contratos;
    }
}
