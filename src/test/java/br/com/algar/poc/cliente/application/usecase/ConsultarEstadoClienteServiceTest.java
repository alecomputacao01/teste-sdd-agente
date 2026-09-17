package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.AcaoDisponivel;
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
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link ConsultarEstadoClienteService} existir (tasks.md T015). Cobre RF-004. */
@ExtendWith(MockitoExtension.class)
class ConsultarEstadoClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Test
    void deveRetornarStatusEAcoesDisponiveisDoClienteAtivo() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new ConsultarEstadoClienteService(repository);
        var estado = service.consultar(cliente.id());

        assertThat(estado.status()).isEqualTo(StatusCliente.ATIVO);
        assertThat(estado.acoesDisponiveis())
                .containsExactlyInAnyOrder(AcaoDisponivel.SUSPENDER_A_PEDIDO, AcaoDisponivel.ALTERAR, AcaoDisponivel.CANCELAR);
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        var id = ClienteId.novo();
        when(repository.findById(id)).thenReturn(Optional.empty());

        var service = new ConsultarEstadoClienteService(repository);

        assertThatThrownBy(() -> service.consultar(id)).isInstanceOf(NoSuchElementException.class);
    }
}
