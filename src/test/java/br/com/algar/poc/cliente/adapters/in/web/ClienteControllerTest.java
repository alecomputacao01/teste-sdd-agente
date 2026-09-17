package br.com.algar.poc.cliente.adapters.in.web;

import br.com.algar.poc.cliente.domain.model.AcaoDisponivel;
import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.EstadoCliente;
import br.com.algar.poc.cliente.domain.model.StatusCliente;
import br.com.algar.poc.cliente.domain.ports.in.AlterarStatusClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarCreditoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarFaturaClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.CadastrarClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarDadosAuditoriaUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarEstadoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarPerfil360UseCase;
import br.com.algar.poc.cliente.domain.ports.in.VerificarElegibilidadeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD: escrito antes de {@link ClienteController} existir (tasks.md T018, evoluído em T019-T025).
 * MockMvc standalone + Mockito, sem contexto Spring — mesma nota de {@code ProductControllerTest}
 * (removido em T001): {@code @WebMvcTest} não sobe contexto utilizável nesta versão do Spring Boot.
 */
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private CadastrarClienteUseCase cadastrarClienteUseCase;
    @Mock
    private AlterarStatusClienteUseCase alterarStatusClienteUseCase;
    @Mock
    private AtualizarFaturaClienteUseCase atualizarFaturaClienteUseCase;
    @Mock
    private AtualizarCreditoClienteUseCase atualizarCreditoClienteUseCase;
    @Mock
    private ConsultarEstadoClienteUseCase consultarEstadoClienteUseCase;
    @Mock
    private VerificarElegibilidadeUseCase verificarElegibilidadeUseCase;
    @Mock
    private ConsultarPerfil360UseCase consultarPerfil360UseCase;
    @Mock
    private ConsultarDadosAuditoriaUseCase consultarDadosAuditoriaUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var controller = new ClienteController(
                cadastrarClienteUseCase,
                alterarStatusClienteUseCase,
                atualizarFaturaClienteUseCase,
                atualizarCreditoClienteUseCase,
                consultarEstadoClienteUseCase,
                verificarElegibilidadeUseCase,
                consultarPerfil360UseCase,
                consultarDadosAuditoriaUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private static Cliente clienteAtivo() {
        return Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
    }

    @Test
    void deveRetornar201AoCadastrarClienteValido() throws Exception {
        var cliente = clienteAtivo();
        when(cadastrarClienteUseCase.cadastrar(any(), any(), any())).thenReturn(cliente);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType("application/json")
                        .content("""
                                {"documento":"52998224725","enderecos":[{"logradouro":"Rua A","numero":"100","cep":"12345-000","cidade":"São Paulo","uf":"SP","principal":true,"validadoNosCorreios":true}],"dadosContato":{"telefone":"11999990000","email":"cliente@exemplo.com"}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar409QuandoCadastroViolaInvarianteDeDominio() throws Exception {
        when(cadastrarClienteUseCase.cadastrar(any(), any(), any()))
                .thenThrow(new IllegalStateException("cadastro não pode ser ativado sem um endereço principal validado nos Correios"));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType("application/json")
                        .content("""
                                {"documento":"52998224725","enderecos":[{"logradouro":"Rua A","numero":"100","cep":"12345-000","cidade":"São Paulo","uf":"SP","principal":false,"validadoNosCorreios":true}],"dadosContato":{"telefone":"11999990000","email":"cliente@exemplo.com"}}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400QuandoDocumentoForInvalido() throws Exception {
        when(cadastrarClienteUseCase.cadastrar(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("documento inválido"));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType("application/json")
                        .content("""
                                {"documento":"123","enderecos":[{"logradouro":"Rua A","numero":"100","cep":"12345-000","cidade":"São Paulo","uf":"SP","principal":true,"validadoNosCorreios":true}],"dadosContato":{"telefone":"11999990000","email":"cliente@exemplo.com"}}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar200AoSuspenderAPedido() throws Exception {
        var cliente = clienteAtivo();
        cliente.suspenderAPedido();
        when(alterarStatusClienteUseCase.alterarStatus(eq(cliente.id()), any())).thenReturn(cliente);

        mockMvc.perform(patch("/api/v1/clientes/" + cliente.id() + "/status")
                        .contentType("application/json")
                        .content("""
                                {"acao":"SUSPENDER_A_PEDIDO"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENSO_A_PEDIDO"));
    }

    @Test
    void deveRetornar409AoTentarReativarClienteCancelado() throws Exception {
        var cliente = clienteAtivo();
        when(alterarStatusClienteUseCase.alterarStatus(eq(cliente.id()), any()))
                .thenThrow(new IllegalStateException("cliente cancelado não pode ser reativado nem sofrer novas cobranças"));

        mockMvc.perform(patch("/api/v1/clientes/" + cliente.id() + "/status")
                        .contentType("application/json")
                        .content("""
                                {"acao":"REATIVAR"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar200AoAtualizarFaturaERefletirAutoSuspensao() throws Exception {
        var cliente = clienteAtivo();
        cliente.avaliarAtrasoFatura(20);
        when(atualizarFaturaClienteUseCase.atualizarFatura(eq(cliente.id()), any(Integer.class))).thenReturn(cliente);

        mockMvc.perform(patch("/api/v1/clientes/" + cliente.id() + "/faturas")
                        .contentType("application/json")
                        .content("""
                                {"diasAtrasoFaturaMaisAntiga":20}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENSO_INADIMPLENCIA"));
    }

    @Test
    void deveRetornar204AoAtualizarCredito() throws Exception {
        var cliente = clienteAtivo();
        when(atualizarCreditoClienteUseCase.atualizarCredito(eq(cliente.id()), any(), any(Boolean.class))).thenReturn(cliente);

        mockMvc.perform(patch("/api/v1/clientes/" + cliente.id() + "/credito")
                        .contentType("application/json")
                        .content("""
                                {"score":"VERDE","possuiContestacaoAbertaUltimos6Meses":false}
                                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarEstadoDoCliente() throws Exception {
        var id = ClienteId.novo();
        var estado = new EstadoCliente(StatusCliente.ATIVO,
                Set.of(AcaoDisponivel.SUSPENDER_A_PEDIDO, AcaoDisponivel.ALTERAR, AcaoDisponivel.CANCELAR));
        when(consultarEstadoClienteUseCase.consultar(id)).thenReturn(estado);

        mockMvc.perform(get("/api/v1/clientes/" + id + "/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.acoesDisponiveis", org.hamcrest.Matchers.hasSize(3)));
    }

    @Test
    void deveRetornarResultadoDeElegibilidade() throws Exception {
        var id = ClienteId.novo();
        when(verificarElegibilidadeUseCase.verificar(id))
                .thenReturn(br.com.algar.poc.cliente.domain.model.ResultadoElegibilidade.criarNaoElegivel(List.of("score não é Verde")));

        mockMvc.perform(get("/api/v1/clientes/" + id + "/elegibilidade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elegivel").value(false))
                .andExpect(jsonPath("$.motivos[0]").value("score não é Verde"));
    }

    @Test
    void deveRetornarPerfil360ComDocumentoCompletoParaPortal() throws Exception {
        var id = ClienteId.novo();
        var perfil = new br.com.algar.poc.cliente.domain.model.Perfil360(
                "52998224725", "11999990000", "cliente@exemplo.com", List.of(), List.of(), List.of());
        when(consultarPerfil360UseCase.consultar(id, br.com.algar.poc.cliente.domain.model.PerfilConsumidor.PORTAL))
                .thenReturn(perfil);

        mockMvc.perform(get("/api/v1/clientes/" + id + "/perfil-360")
                        .header("X-Perfil-Consumidor", "PORTAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("52998224725"));
    }

    @Test
    void deveRetornarPerfil360ComDocumentoMascaradoParaCrm() throws Exception {
        var id = ClienteId.novo();
        var perfil = new br.com.algar.poc.cliente.domain.model.Perfil360(
                "*********25", "*********00", "cliente@exemplo.com", List.of(), List.of(), List.of());
        when(consultarPerfil360UseCase.consultar(id, br.com.algar.poc.cliente.domain.model.PerfilConsumidor.CRM))
                .thenReturn(perfil);

        mockMvc.perform(get("/api/v1/clientes/" + id + "/perfil-360")
                        .header("X-Perfil-Consumidor", "CRM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("*********25"));
    }

    @Test
    void deveRetornarDadosDeAuditoriaSemMascara() throws Exception {
        var id = ClienteId.novo();
        var dados = new br.com.algar.poc.cliente.domain.model.DadosAuditoria(
                "52998224725", "11999990000", "cliente@exemplo.com", StatusCliente.ATIVO, List.of(), List.of(), List.of());
        when(consultarDadosAuditoriaUseCase.consultar(id)).thenReturn(dados);

        mockMvc.perform(get("/api/v1/clientes/" + id + "/auditoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("52998224725"))
                .andExpect(jsonPath("$.telefone").value("11999990000"));
    }
}
