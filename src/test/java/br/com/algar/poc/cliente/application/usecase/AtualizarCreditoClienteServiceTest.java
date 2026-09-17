package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.ScoreCredito;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link AtualizarCreditoClienteService} existir (tasks.md T014). Cobre RF-005. */
@ExtendWith(MockitoExtension.class)
class AtualizarCreditoClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Test
    void deveArmazenarScoreEContestacaoNoCliente() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new AtualizarCreditoClienteService(repository);
        var atualizado = service.atualizarCredito(cliente.id(), ScoreCredito.VERDE, false);

        assertThat(atualizado.verificarElegibilidade().elegivel()).isTrue();
    }
}
