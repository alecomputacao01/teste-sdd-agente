package br.com.algar.poc.cliente.application.usecase;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link AtualizarFaturaClienteService} existir (tasks.md T013). Cobre RF-002. */
@ExtendWith(MockitoExtension.class)
class AtualizarFaturaClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    private static Cliente clienteAtivo() {
        return Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
    }

    @Test
    void deveSuspenderPorInadimplenciaQuandoAtrasoMaiorQue15Dias() {
        var cliente = clienteAtivo();
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new AtualizarFaturaClienteService(repository);
        var atualizado = service.atualizarFatura(cliente.id(), 20);

        assertThat(atualizado.status()).isEqualTo(StatusCliente.SUSPENSO_INADIMPLENCIA);
    }

    @Test
    void naoDeveSuspenderComAteQuinzeDiasDeAtraso() {
        var cliente = clienteAtivo();
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new AtualizarFaturaClienteService(repository);
        var atualizado = service.atualizarFatura(cliente.id(), 10);

        assertThat(atualizado.status()).isEqualTo(StatusCliente.ATIVO);
    }
}
