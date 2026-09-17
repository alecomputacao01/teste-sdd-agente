package br.com.algar.poc.cliente.config;

import br.com.algar.poc.cliente.application.usecase.AlterarStatusClienteService;
import br.com.algar.poc.cliente.application.usecase.AtualizarCreditoClienteService;
import br.com.algar.poc.cliente.application.usecase.AtualizarFaturaClienteService;
import br.com.algar.poc.cliente.application.usecase.CadastrarClienteService;
import br.com.algar.poc.cliente.application.usecase.ConsultarDadosAuditoriaService;
import br.com.algar.poc.cliente.application.usecase.ConsultarEstadoClienteService;
import br.com.algar.poc.cliente.application.usecase.ConsultarPerfil360Service;
import br.com.algar.poc.cliente.application.usecase.VerificarElegibilidadeService;
import br.com.algar.poc.cliente.domain.ports.in.AlterarStatusClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarCreditoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarFaturaClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.CadastrarClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarDadosAuditoriaUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarEstadoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarPerfil360UseCase;
import br.com.algar.poc.cliente.domain.ports.in.VerificarElegibilidadeUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Liga cada porta de entrada à sua implementação. As classes de application.usecase permanecem
 * livres de anotações Spring de propósito — a fiação explícita mantém a camada de aplicação
 * portável e testável sem contexto (ver *ServiceTest, todos sem @SpringBootTest).
 */
@Configuration
public class BeanConfig {

    @Bean
    public CadastrarClienteUseCase cadastrarClienteUseCase(ClienteRepository repository) {
        return new CadastrarClienteService(repository);
    }

    @Bean
    public AlterarStatusClienteUseCase alterarStatusClienteUseCase(ClienteRepository repository) {
        return new AlterarStatusClienteService(repository);
    }

    @Bean
    public AtualizarFaturaClienteUseCase atualizarFaturaClienteUseCase(ClienteRepository repository) {
        return new AtualizarFaturaClienteService(repository);
    }

    @Bean
    public AtualizarCreditoClienteUseCase atualizarCreditoClienteUseCase(ClienteRepository repository) {
        return new AtualizarCreditoClienteService(repository);
    }

    @Bean
    public ConsultarEstadoClienteUseCase consultarEstadoClienteUseCase(ClienteRepository repository) {
        return new ConsultarEstadoClienteService(repository);
    }

    @Bean
    public VerificarElegibilidadeUseCase verificarElegibilidadeUseCase(ClienteRepository repository) {
        return new VerificarElegibilidadeService(repository);
    }

    @Bean
    public ConsultarPerfil360UseCase consultarPerfil360UseCase(ClienteRepository repository) {
        return new ConsultarPerfil360Service(repository);
    }

    @Bean
    public ConsultarDadosAuditoriaUseCase consultarDadosAuditoriaUseCase(ClienteRepository repository) {
        return new ConsultarDadosAuditoriaService(repository);
    }
}
