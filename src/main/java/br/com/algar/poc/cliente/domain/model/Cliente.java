package br.com.algar.poc.cliente.domain.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate Root do bounded context Cliente. Gerencia autonomamente seu status — nenhum consumidor
 * (CRM, Portal) altera o status diretamente; só por métodos de transição de domínio (spec.md RF-001).
 * Identidade única por {@link Documento}, com múltiplos {@link Endereco} e {@link ContaFaturamento}
 * vinculados (RF-008). Sem dependência de framework — verificado pelo ArchUnit
 * (arch.HexagonalArchitectureTest).
 */
public final class Cliente {

    private final ClienteId id;
    private final Documento documento;
    private StatusCliente status;
    private DadosContato dadosContato;
    private final List<Endereco> enderecos = new ArrayList<>();
    private final List<ContaFaturamento> contasFaturamento = new ArrayList<>();
    private final List<Contrato> contratos = new ArrayList<>();
    private ScoreCredito scoreCredito;
    private boolean possuiContestacaoAbertaUltimos6Meses;

    private Cliente(ClienteId id, Documento documento, DadosContato dadosContato) {
        this.id = id;
        this.documento = documento;
        this.dadosContato = dadosContato;
        this.status = StatusCliente.ATIVO;
    }

    /**
     * RF-008/RF-009 — identidade única com múltiplos endereços. RF-010 — só ativa (status ATIVO) se
     * houver ao menos um endereço principal já validado nos Correios; caso contrário a criação
     * inteira é rejeitada (plan.md §0.1: o flag chega pronto, sem porta de saída dedicada).
     */
    public static Cliente registrar(ClienteId id, Documento documento, List<Endereco> enderecos, DadosContato dadosContato) {
        Objects.requireNonNull(id, "id do cliente não pode ser nulo");
        Objects.requireNonNull(documento, "documento do cliente não pode ser nulo");
        Objects.requireNonNull(dadosContato, "dados de contato do cliente não podem ser nulos");
        var listaEnderecos = enderecos == null ? List.<Endereco>of() : enderecos;
        boolean possuiPrincipalValidado = listaEnderecos.stream()
                .anyMatch(endereco -> endereco.principal() && endereco.validadoNosCorreios());
        if (!possuiPrincipalValidado) {
            throw new IllegalStateException(
                    "cadastro não pode ser ativado sem um endereço principal validado nos Correios");
        }
        var cliente = new Cliente(id, documento, dadosContato);
        cliente.enderecos.addAll(listaEnderecos);
        return cliente;
    }

    /**
     * Reidrata um Cliente já existente a partir da persistência (adapters/out) — não reaplica a
     * validação de ativação de RF-010, que só faz sentido no cadastro (o estado já foi validado uma
     * vez, na chamada original a {@link #registrar}).
     */
    public static Cliente reconstituir(ClienteId id, Documento documento, StatusCliente status, DadosContato dadosContato,
                                        List<Endereco> enderecos, List<ContaFaturamento> contasFaturamento,
                                        List<Contrato> contratos, ScoreCredito scoreCredito,
                                        boolean possuiContestacaoAbertaUltimos6Meses) {
        var cliente = new Cliente(id, documento, dadosContato);
        cliente.status = status;
        cliente.enderecos.addAll(enderecos);
        cliente.contasFaturamento.addAll(contasFaturamento);
        cliente.contratos.addAll(contratos);
        cliente.scoreCredito = scoreCredito;
        cliente.possuiContestacaoAbertaUltimos6Meses = possuiContestacaoAbertaUltimos6Meses;
        return cliente;
    }

    public void adicionarContaFaturamento(ContaFaturamento conta) {
        Objects.requireNonNull(conta, "conta de faturamento não pode ser nula");
        contasFaturamento.add(conta);
    }

    public ClienteId id() {
        return id;
    }

    public Documento documento() {
        return documento;
    }

    public StatusCliente status() {
        return status;
    }

    public DadosContato dadosContato() {
        return dadosContato;
    }

    public List<Endereco> enderecos() {
        return List.copyOf(enderecos);
    }

    public List<ContaFaturamento> contasFaturamento() {
        return List.copyOf(contasFaturamento);
    }

    /** RF-011 — vincula uma referência somente-leitura de contrato para compor o perfil-360. */
    public void vincularContrato(Contrato contrato) {
        Objects.requireNonNull(contrato, "contrato não pode ser nulo");
        contratos.add(contrato);
    }

    public List<Contrato> contratos() {
        return List.copyOf(contratos);
    }

    public ScoreCredito scoreCredito() {
        return scoreCredito;
    }

    public boolean possuiContestacaoAbertaUltimos6Meses() {
        return possuiContestacaoAbertaUltimos6Meses;
    }

    /** RF-002 — só suspende por inadimplência se a fatura mais antiga tiver mais de 15 dias de atraso. */
    public void avaliarAtrasoFatura(int diasAtraso) {
        requireNaoCancelado();
        if (diasAtraso > 15) {
            this.status = StatusCliente.SUSPENSO_INADIMPLENCIA;
        }
    }

    /** RF-003 — cliente cancelado é tratado como histórico: nenhuma transição volta a ativá-lo. */
    public void cancelar() {
        requireNaoCancelado();
        this.status = StatusCliente.CANCELADO;
    }

    public void reativar() {
        requireNaoCancelado();
        this.status = StatusCliente.ATIVO;
    }

    public void suspenderAPedido() {
        requireNaoCancelado();
        this.status = StatusCliente.SUSPENSO_A_PEDIDO;
    }

    /** RF-004 — estado atual determina quais ações o CRM/Portal podem oferecer ao usuário. */
    public Set<AcaoDisponivel> obterAcoesDisponiveis() {
        return switch (status) {
            case ATIVO -> EnumSet.of(AcaoDisponivel.SUSPENDER_A_PEDIDO, AcaoDisponivel.ALTERAR, AcaoDisponivel.CANCELAR);
            case SUSPENSO_A_PEDIDO, SUSPENSO_INADIMPLENCIA -> EnumSet.of(AcaoDisponivel.REATIVAR, AcaoDisponivel.CANCELAR);
            case CANCELADO -> Set.of();
        };
    }

    /** RF-005 — armazena o score interno (já "limpo") e a flag de contestação, vindos de crédito. */
    public void atualizarDadosCredito(ScoreCredito scoreCredito, boolean possuiContestacaoAbertaUltimos6Meses) {
        requireNaoCancelado();
        this.scoreCredito = Objects.requireNonNull(scoreCredito, "score de crédito não pode ser nulo");
        this.possuiContestacaoAbertaUltimos6Meses = possuiContestacaoAbertaUltimos6Meses;
    }

    /** RF-006 — elegível apenas com score interno "Verde" e sem contestação de fatura aberta. */
    public ResultadoElegibilidade verificarElegibilidade() {
        var motivos = new ArrayList<String>();
        if (scoreCredito == null) {
            motivos.add("score de crédito ainda não informado");
        } else if (scoreCredito != ScoreCredito.VERDE) {
            motivos.add("score interno de adimplência não está \"Verde\"");
        }
        if (possuiContestacaoAbertaUltimos6Meses) {
            motivos.add("há contestação de fatura aberta nos últimos 6 meses");
        }
        return motivos.isEmpty() ? ResultadoElegibilidade.criarElegivel() : ResultadoElegibilidade.criarNaoElegivel(motivos);
    }

    private void requireNaoCancelado() {
        if (this.status == StatusCliente.CANCELADO) {
            throw new IllegalStateException("cliente cancelado não pode ser reativado nem sofrer novas cobranças");
        }
    }
}
