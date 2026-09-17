package br.com.algar.poc.cliente.adapters.out.persistence;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ContaFaturamento;
import br.com.algar.poc.cliente.domain.model.ContaFaturamentoId;
import br.com.algar.poc.cliente.domain.model.Contrato;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.ScoreCredito;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração real contra PostgreSQL via Testcontainers (tasks.md T026 — camada de
 * infraestrutura só é validada aqui, nunca em teste unitário puro de domínio/application).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ClienteRepositoryAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("cliente")
            .withUsername("cliente")
            .withPassword("cliente");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ClienteRepositoryAdapter adapter;

    private static Endereco enderecoPrincipal() {
        return Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);
    }

    @Test
    void deveSalvarERecuperarClienteComArvoreCompleta() {
        var enderecoSecundario = Endereco.de("Rua B", "200", "54321-000", "Campinas", "SP", false, false);
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("52998224725"),
                List.of(enderecoPrincipal(), enderecoSecundario),
                DadosContato.de("11999990000", "cliente@exemplo.com"));
        cliente.adicionarContaFaturamento(
                ContaFaturamento.criar(ContaFaturamentoId.novo(), "Banda larga", enderecoPrincipal()));
        cliente.vincularContrato(new Contrato("CTR-1", "Banda larga 500MB", "ATIVO"));
        cliente.atualizarDadosCredito(ScoreCredito.VERDE, false);

        adapter.save(cliente);

        var encontrado = adapter.findById(cliente.id());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().documento()).isEqualTo(cliente.documento());
        assertThat(encontrado.get().enderecos()).hasSize(2);
        assertThat(encontrado.get().contasFaturamento()).hasSize(1);
        assertThat(encontrado.get().contratos()).hasSize(1);
        assertThat(encontrado.get().scoreCredito()).isEqualTo(ScoreCredito.VERDE);
    }

    @Test
    void deveRefletirTransicaoDeStatusAposSalvarNovamente() {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de("11144477735"),
                List.of(enderecoPrincipal()), DadosContato.de("11999990000", "cliente@exemplo.com"));
        adapter.save(cliente);

        cliente.suspenderAPedido();
        adapter.save(cliente);

        var encontrado = adapter.findById(cliente.id());
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().status()).isEqualTo(br.com.algar.poc.cliente.domain.model.StatusCliente.SUSPENSO_A_PEDIDO);
    }
}
