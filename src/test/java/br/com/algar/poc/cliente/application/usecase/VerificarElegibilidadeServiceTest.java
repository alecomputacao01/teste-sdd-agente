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
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link VerificarElegibilidadeService} existir (tasks.md T014). Cobre RF-006/RF-007. */
@ExtendWith(MockitoExtension.class)
class VerificarElegibilidadeServiceTest {

    @Mock
    private ClienteRepository repository;

    @Test
    void deveRetornarResultadoDeElegibilidadeDoClienteEncontrado() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        cliente.atualizarDadosCredito(ScoreCredito.VERMELHO, false);
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new VerificarElegibilidadeService(repository);
        var resultado = service.verificar(cliente.id());

        assertThat(resultado.elegivel()).isFalse();
        assertThat(resultado.motivos()).isNotEmpty();
    }
}
