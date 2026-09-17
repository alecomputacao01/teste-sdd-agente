package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.AcaoTransicaoStatus;
import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.StatusCliente;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * TDD: escrito antes de {@link AlterarStatusClienteService} existir (tasks.md T012).
 * Cobre RF-001/RF-003.
 */
@ExtendWith(MockitoExtension.class)
class AlterarStatusClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Test
    void deveSuspenderAPedido() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new AlterarStatusClienteService(repository);
        var atualizado = service.alterarStatus(cliente.id(), AcaoTransicaoStatus.SUSPENDER_A_PEDIDO);

        assertThat(atualizado.status()).isEqualTo(StatusCliente.SUSPENSO_A_PEDIDO);
    }

    @Test
    void naoDeveReativarClienteCancelado() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        cliente.cancelar();
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new AlterarStatusClienteService(repository);

        assertThatThrownBy(() -> service.alterarStatus(cliente.id(), AcaoTransicaoStatus.REATIVAR))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        var id = ClienteId.novo();
        when(repository.findById(id)).thenReturn(Optional.empty());

        var service = new AlterarStatusClienteService(repository);

        assertThatThrownBy(() -> service.alterarStatus(id, AcaoTransicaoStatus.CANCELAR))
                .isInstanceOf(NoSuchElementException.class);
    }
}
