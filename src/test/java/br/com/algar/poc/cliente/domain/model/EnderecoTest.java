package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** TDD: escrito antes de {@link Endereco} existir (tasks.md T006). Cobre RF-009. */
class EnderecoTest {

    @Test
    void doisEnderecosComOsMesmosValoresSaoIguais() {
        var a = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);
        var b = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", true, true);

        assertThat(a).isEqualTo(b);
    }

    @Test
    void alterarPrincipalGeraNovaInstanciaSemMudarAOriginal() {
        var original = Endereco.de("Rua A", "100", "12345-000", "São Paulo", "SP", false, true);

        var comoPrincipal = original.comoPrincipal();

        assertThat(original.principal()).isFalse();
        assertThat(comoPrincipal.principal()).isTrue();
        assertThat(comoPrincipal).isNotSameAs(original);
    }

    @Test
    void naoAceitaCepEmBranco() {
        assertThatThrownBy(() -> Endereco.de("Rua A", "100", " ", "São Paulo", "SP", true, true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
