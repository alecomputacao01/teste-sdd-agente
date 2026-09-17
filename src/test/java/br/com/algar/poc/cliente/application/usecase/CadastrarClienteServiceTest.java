package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TDD: escrito antes de {@link CadastrarClienteService} existir (tasks.md T011).
 * Unitário puro — repositório mockado (plan.md §3). Cobre RF-008/RF-009/RF-010.
 */
@ExtendWith(MockitoExtension.class)
class CadastrarClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    private static final DadosContato DADOS_CONTATO = DadosContato.de("11999990000", "cliente@exemplo.com");

    @Test
    void deveCadastrarClienteComEnderecoPrincipalValidado() {
        var enderecoPrincipal = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new CadastrarClienteService(repository);
        var cliente = service.cadastrar("529.982.247-25", List.of(enderecoPrincipal), DADOS_CONTATO);

        assertThat(cliente.documento().value()).isEqualTo("52998224725");
        verify(repository).save(any(Cliente.class));
    }

    @Test
    void naoDeveCadastrarSemEnderecoPrincipalValidado() {
        var enderecoSecundario = Endereco.de("Rua B", "200", "54321-000", "Campinas", "SP", false, true);

        var service = new CadastrarClienteService(repository);

        assertThatThrownBy(() -> service.cadastrar("529.982.247-25", List.of(enderecoSecundario), DADOS_CONTATO))
                .isInstanceOf(IllegalStateException.class);
    }
}
