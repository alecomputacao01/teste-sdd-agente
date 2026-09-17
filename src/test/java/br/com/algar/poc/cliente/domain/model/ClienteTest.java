package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD: escrito antes de {@link Cliente} existir (tasks.md T002, evoluído em T003-T008).
 * Cobre RF-001 a RF-004, RF-008 e RF-010 — a raiz do agregado gerencia autonomamente seu status
 * (sem setters públicos), sua identidade e a validação de ativação do cadastro.
 */
class ClienteTest {

    private static final Documento DOCUMENTO = Documento.de("529.982.247-25");
    private static final DadosContato DADOS_CONTATO = DadosContato.de("11999990000", "cliente@exemplo.com");

    private static Endereco enderecoPrincipalValido() {
        return Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);
    }

    private static Cliente clienteAtivo() {
        return Cliente.registrar(ClienteId.novo(), DOCUMENTO, List.of(enderecoPrincipalValido()), DADOS_CONTATO);
    }

    @Test
    void deveIniciarAtivoAoRegistrarComEnderecoPrincipalValidado() {
        var cliente = clienteAtivo();

        assertThat(cliente.status()).isEqualTo(StatusCliente.ATIVO);
    }

    @Test
    void naoDeveExporSetterPublicoDeStatus() {
        boolean existeSetter = Arrays.stream(Cliente.class.getMethods())
                .map(Method::getName)
                .anyMatch(nome -> nome.toLowerCase().contains("setstatus"));

        assertThat(existeSetter).isFalse();
    }

    @Test
    void deveSuspenderPorInadimplenciaQuandoFaturaTemMaisDe15DiasDeAtraso() {
        var cliente = clienteAtivo();

        cliente.avaliarAtrasoFatura(16);

        assertThat(cliente.status()).isEqualTo(StatusCliente.SUSPENSO_INADIMPLENCIA);
    }

    @Test
    void naoDeveSuspenderPorInadimplenciaComAteQuinzeDiasDeAtraso() {
        var cliente = clienteAtivo();

        cliente.avaliarAtrasoFatura(15);

        assertThat(cliente.status()).isEqualTo(StatusCliente.ATIVO);
    }

    @Test
    void clienteCanceladoNaoPodeReativar() {
        var cliente = clienteAtivo();
        cliente.cancelar();

        assertThatThrownBy(cliente::reativar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cancelado");
        assertThat(cliente.status()).isEqualTo(StatusCliente.CANCELADO);
    }

    @Test
    void clienteCanceladoNaoSofreNovaAvaliacaoDeAtrasoDeFatura() {
        var cliente = clienteAtivo();
        cliente.cancelar();

        assertThatThrownBy(() -> cliente.avaliarAtrasoFatura(30))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cancelado");
        assertThat(cliente.status()).isEqualTo(StatusCliente.CANCELADO);
    }

    @Test
    void acoesDisponiveisParaClienteAtivo() {
        var cliente = clienteAtivo();

        assertThat(cliente.obterAcoesDisponiveis())
                .containsExactlyInAnyOrder(AcaoDisponivel.SUSPENDER_A_PEDIDO, AcaoDisponivel.ALTERAR, AcaoDisponivel.CANCELAR);
    }

    @Test
    void acoesDisponiveisParaClienteSuspenso() {
        var suspensoAPedido = clienteAtivo();
        suspensoAPedido.suspenderAPedido();
        assertThat(suspensoAPedido.obterAcoesDisponiveis())
                .containsExactlyInAnyOrder(AcaoDisponivel.REATIVAR, AcaoDisponivel.CANCELAR);

        var suspensoInadimplencia = clienteAtivo();
        suspensoInadimplencia.avaliarAtrasoFatura(16);
        assertThat(suspensoInadimplencia.obterAcoesDisponiveis())
                .containsExactlyInAnyOrder(AcaoDisponivel.REATIVAR, AcaoDisponivel.CANCELAR);
    }

    @Test
    void nenhumaAcaoDisponivelParaClienteCancelado() {
        var cliente = clienteAtivo();
        cliente.cancelar();

        assertThat(cliente.obterAcoesDisponiveis()).isEqualTo(Set.of());
    }

    @Test
    void clienteSuportaMultiplosEnderecosEContasDeFaturamento() {
        var enderecoSecundario = Endereco.de("Rua B", "200", "54321-000", "Campinas", "SP", false, false);
        var cliente = Cliente.registrar(ClienteId.novo(), DOCUMENTO,
                List.of(enderecoPrincipalValido(), enderecoSecundario), DADOS_CONTATO);

        var contaBandaLarga = ContaFaturamento.criar(ContaFaturamentoId.novo(), "Banda larga", enderecoPrincipalValido());
        var contaCelular = ContaFaturamento.criar(ContaFaturamentoId.novo(), "Celular", enderecoSecundario);
        cliente.adicionarContaFaturamento(contaBandaLarga);
        cliente.adicionarContaFaturamento(contaCelular);

        assertThat(cliente.documento()).isEqualTo(DOCUMENTO);
        assertThat(cliente.enderecos()).containsExactlyInAnyOrder(enderecoPrincipalValido(), enderecoSecundario);
        assertThat(cliente.contasFaturamento()).containsExactlyInAnyOrder(contaBandaLarga, contaCelular);
    }

    @Test
    void clienteVinculaContratosComoReferenciaSomenteLeitura() {
        var cliente = clienteAtivo();
        var contrato = new Contrato("CTR-1", "Banda larga 500MB", "ATIVO");

        cliente.vincularContrato(contrato);

        assertThat(cliente.contratos()).containsExactly(contrato);
    }

    @Test
    void naoDeveAtivarCadastroSemEnderecoPrincipal() {
        var enderecoSecundario = Endereco.de("Rua B", "200", "54321-000", "Campinas", "SP", false, true);

        assertThatThrownBy(() -> Cliente.registrar(ClienteId.novo(), DOCUMENTO, List.of(enderecoSecundario), DADOS_CONTATO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Correios");
    }

    @Test
    void naoDeveAtivarCadastroComEnderecoPrincipalNaoValidadoNosCorreios() {
        var enderecoPrincipalNaoValidado = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, false);

        assertThatThrownBy(() -> Cliente.registrar(ClienteId.novo(), DOCUMENTO, List.of(enderecoPrincipalNaoValidado), DADOS_CONTATO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Correios");
    }

    @Test
    void elegivelQuandoScoreVerdeESemContestacaoAberta() {
        var cliente = clienteAtivo();
        cliente.atualizarDadosCredito(ScoreCredito.VERDE, false);

        var resultado = cliente.verificarElegibilidade();

        assertThat(resultado.elegivel()).isTrue();
        assertThat(resultado.motivos()).isEmpty();
    }

    @Test
    void naoElegivelQuandoScoreNaoEVerde() {
        var cliente = clienteAtivo();
        cliente.atualizarDadosCredito(ScoreCredito.AMARELO, false);

        var resultado = cliente.verificarElegibilidade();

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.motivos()).isNotEmpty();
    }

    @Test
    void naoElegivelQuandoHaContestacaoAbertaNosUltimos6Meses() {
        var cliente = clienteAtivo();
        cliente.atualizarDadosCredito(ScoreCredito.VERDE, true);

        var resultado = cliente.verificarElegibilidade();

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.motivos()).isNotEmpty();
    }

    @Test
    void naoElegivelQuandoScoreDeCreditoAindaNaoFoiInformado() {
        var cliente = clienteAtivo();

        var resultado = cliente.verificarElegibilidade();

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.motivos()).isNotEmpty();
    }

    @Test
    void naoExpoeDadosSensiveisNaSuperficiePublica() {
        var termosProibidos = List.of("senha", "roteador", "token", "pagamento");

        boolean expoeDadoSensivel = Arrays.stream(Cliente.class.getMethods())
                .map(Method::getName)
                .map(String::toLowerCase)
                .anyMatch(nome -> termosProibidos.stream().anyMatch(nome::contains));

        assertThat(expoeDadoSensivel).isFalse();
    }
}
