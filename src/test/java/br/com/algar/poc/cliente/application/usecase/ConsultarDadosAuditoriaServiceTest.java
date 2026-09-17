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

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link ConsultarDadosAuditoriaService} existir (tasks.md T017). Cobre RF-012/RF-013. */
@ExtendWith(MockitoExtension.class)
class ConsultarDadosAuditoriaServiceTest {

    @Mock
    private ClienteRepository repository;

    @Test
    void deveRetornarDadosCompletosSemMascara() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true)),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new ConsultarDadosAuditoriaService(repository);
        var dados = service.consultar(cliente.id());

        assertThat(dados.documento()).isEqualTo("52998224725");
        assertThat(dados.telefone()).isEqualTo("11999990000");
        assertThat(dados.status()).isEqualTo(StatusCliente.ATIVO);
    }

    @Test
    void naoExpoeDadosSensiveisNaEstruturaDeAuditoria() {
        var termosProibidos = List.of("senha", "roteador", "token", "pagamento");

        boolean expoeDadoSensivel = Arrays.stream(br.com.algar.poc.cliente.domain.model.DadosAuditoria.class.getRecordComponents())
                .map(RecordComponent::getName)
                .map(String::toLowerCase)
                .anyMatch(nome -> termosProibidos.stream().anyMatch(nome::contains));

        assertThat(expoeDadoSensivel).isFalse();
    }
}
