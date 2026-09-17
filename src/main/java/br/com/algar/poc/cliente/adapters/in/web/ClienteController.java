package br.com.algar.poc.cliente.adapters.in.web;

import br.com.algar.poc.cliente.adapters.in.web.dto.AlterarStatusClienteRequest;
import br.com.algar.poc.cliente.adapters.in.web.dto.AtualizarCreditoClienteRequest;
import br.com.algar.poc.cliente.adapters.in.web.dto.AtualizarFaturaClienteRequest;
import br.com.algar.poc.cliente.adapters.in.web.dto.AuditoriaResponse;
import br.com.algar.poc.cliente.adapters.in.web.dto.CadastrarClienteRequest;
import br.com.algar.poc.cliente.adapters.in.web.dto.ClienteResponse;
import br.com.algar.poc.cliente.adapters.in.web.dto.EnderecoRequest;
import br.com.algar.poc.cliente.adapters.in.web.dto.ElegibilidadeResponse;
import br.com.algar.poc.cliente.adapters.in.web.dto.EstadoClienteResponse;
import br.com.algar.poc.cliente.adapters.in.web.dto.Perfil360Response;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.PerfilConsumidor;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.ports.in.AlterarStatusClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarCreditoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarFaturaClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.CadastrarClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarDadosAuditoriaUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarEstadoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarPerfil360UseCase;
import br.com.algar.poc.cliente.domain.ports.in.VerificarElegibilidadeUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Adapter de entrada (driving adapter) — só orquestra, não contém regra de negócio. Endpoints
 * adicionados incrementalmente (tasks.md T018-T025), um bloco de RFs por vez.
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final AlterarStatusClienteUseCase alterarStatusClienteUseCase;
    private final AtualizarFaturaClienteUseCase atualizarFaturaClienteUseCase;
    private final AtualizarCreditoClienteUseCase atualizarCreditoClienteUseCase;
    private final ConsultarEstadoClienteUseCase consultarEstadoClienteUseCase;
    private final VerificarElegibilidadeUseCase verificarElegibilidadeUseCase;
    private final ConsultarPerfil360UseCase consultarPerfil360UseCase;
    private final ConsultarDadosAuditoriaUseCase consultarDadosAuditoriaUseCase;

    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            AlterarStatusClienteUseCase alterarStatusClienteUseCase,
            AtualizarFaturaClienteUseCase atualizarFaturaClienteUseCase,
            AtualizarCreditoClienteUseCase atualizarCreditoClienteUseCase,
            ConsultarEstadoClienteUseCase consultarEstadoClienteUseCase,
            VerificarElegibilidadeUseCase verificarElegibilidadeUseCase,
            ConsultarPerfil360UseCase consultarPerfil360UseCase,
            ConsultarDadosAuditoriaUseCase consultarDadosAuditoriaUseCase) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.alterarStatusClienteUseCase = alterarStatusClienteUseCase;
        this.atualizarFaturaClienteUseCase = atualizarFaturaClienteUseCase;
        this.atualizarCreditoClienteUseCase = atualizarCreditoClienteUseCase;
        this.consultarEstadoClienteUseCase = consultarEstadoClienteUseCase;
        this.verificarElegibilidadeUseCase = verificarElegibilidadeUseCase;
        this.consultarPerfil360UseCase = consultarPerfil360UseCase;
        this.consultarDadosAuditoriaUseCase = consultarDadosAuditoriaUseCase;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody CadastrarClienteRequest request) {
        var enderecos = toEnderecos(request.enderecos());
        var dadosContato = br.com.algar.poc.cliente.domain.model.DadosContato.de(
                request.dadosContato().telefone(), request.dadosContato().email());
        var cliente = cadastrarClienteUseCase.cadastrar(request.documento(), enderecos, dadosContato);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.from(cliente));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponse> alterarStatus(@PathVariable String id, @Valid @RequestBody AlterarStatusClienteRequest request) {
        var cliente = alterarStatusClienteUseCase.alterarStatus(ClienteId.de(id), request.acao());
        return ResponseEntity.ok(ClienteResponse.from(cliente));
    }

    @PatchMapping("/{id}/faturas")
    public ResponseEntity<ClienteResponse> atualizarFatura(@PathVariable String id, @Valid @RequestBody AtualizarFaturaClienteRequest request) {
        var cliente = atualizarFaturaClienteUseCase.atualizarFatura(ClienteId.de(id), request.diasAtrasoFaturaMaisAntiga());
        return ResponseEntity.ok(ClienteResponse.from(cliente));
    }

    @PatchMapping("/{id}/credito")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizarCredito(@PathVariable String id, @Valid @RequestBody AtualizarCreditoClienteRequest request) {
        atualizarCreditoClienteUseCase.atualizarCredito(ClienteId.de(id), request.score(), request.possuiContestacaoAbertaUltimos6Meses());
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoClienteResponse> consultarEstado(@PathVariable String id) {
        var estado = consultarEstadoClienteUseCase.consultar(ClienteId.de(id));
        return ResponseEntity.ok(EstadoClienteResponse.from(estado));
    }

    @GetMapping("/{id}/elegibilidade")
    public ResponseEntity<ElegibilidadeResponse> verificarElegibilidade(@PathVariable String id) {
        var resultado = verificarElegibilidadeUseCase.verificar(ClienteId.de(id));
        return ResponseEntity.ok(ElegibilidadeResponse.from(resultado));
    }

    @GetMapping("/{id}/perfil-360")
    public ResponseEntity<Perfil360Response> consultarPerfil360(
            @PathVariable String id,
            @RequestHeader(value = "X-Perfil-Consumidor", defaultValue = "PORTAL") PerfilConsumidor perfilConsumidor) {
        var perfil = consultarPerfil360UseCase.consultar(ClienteId.de(id), perfilConsumidor);
        return ResponseEntity.ok(Perfil360Response.from(perfil));
    }

    @GetMapping("/{id}/auditoria")
    public ResponseEntity<AuditoriaResponse> consultarAuditoria(@PathVariable String id) {
        var dados = consultarDadosAuditoriaUseCase.consultar(ClienteId.de(id));
        return ResponseEntity.ok(AuditoriaResponse.from(dados));
    }

    private static List<Endereco> toEnderecos(List<EnderecoRequest> requests) {
        return requests.stream()
                .map(r -> Endereco.de(r.logradouro(), r.numero(), r.cep(), r.cidade(), r.uf(), r.principal(), r.validadoNosCorreios()))
                .toList();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public void handleConflict() {
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleBadRequest() {
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
    }
}
