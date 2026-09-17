package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.Contrato;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.PerfilConsumidor;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/** TDD: escrito antes de {@link ConsultarPerfil360Service} existir (tasks.md T016). Cobre RF-011/RF-013. */
@ExtendWith(MockitoExtension.class)
class ConsultarPerfil360ServiceTest {

    @Mock
    private ClienteRepository repository;

    private static Cliente clienteCompleto() {
        var endereco = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"), List.of(endereco),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        cliente.adicionarContaFaturamento(
                br.com.algar.poc.cliente.domain.model.ContaFaturamento.criar(
                        br.com.algar.poc.cliente.domain.model.ContaFaturamentoId.novo(), "Banda larga", endereco));
        cliente.vincularContrato(new Contrato("CTR-1", "Banda larga 500MB", "ATIVO"));
        return cliente;
    }

    @Test
    void perfilPortalRetornaDocumentoETelefoneCompletos() {
        var cliente = clienteCompleto();
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new ConsultarPerfil360Service(repository);
        var perfil = service.consultar(cliente.id(), PerfilConsumidor.PORTAL);

        assertThat(perfil.documento()).isEqualTo("52998224725");
        assertThat(perfil.telefone()).isEqualTo("11999990000");
        assertThat(perfil.enderecos()).hasSize(1);
        assertThat(perfil.contasFaturamento()).hasSize(1);
        assertThat(perfil.contratos()).hasSize(1);
    }

    @Test
    void perfilCrmRetornaDocumentoETelefoneMascarados() {
        var cliente = clienteCompleto();
        when(repository.findById(cliente.id())).thenReturn(Optional.of(cliente));

        var service = new ConsultarPerfil360Service(repository);
        var perfil = service.consultar(cliente.id(), PerfilConsumidor.CRM);

        assertThat(perfil.documento()).endsWith("25").contains("*");
        assertThat(perfil.telefone()).endsWith("00").contains("*");
    }
}
