package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** TDD: escrito antes de {@link DadosContato} existir (tasks.md T006). Cobre RF-009. */
class DadosContatoTest {

    @Test
    void doisDadosContatoComOsMesmosValoresSaoIguais() {
        var a = DadosContato.de("11999990000", "cliente@exemplo.com");
        var b = DadosContato.de("11999990000", "cliente@exemplo.com");

        assertThat(a).isEqualTo(b);
    }

    @Test
    void alterarTelefoneGeraNovaInstanciaSemMudarAOriginal() {
        var original = DadosContato.de("11999990000", "cliente@exemplo.com");

        var atualizado = original.comTelefone("11888880000");

        assertThat(original.telefone()).isEqualTo("11999990000");
        assertThat(atualizado.telefone()).isEqualTo("11888880000");
        assertThat(atualizado).isNotSameAs(original);
    }

    @Test
    void naoAceitaEmailEmBranco() {
        assertThatThrownBy(() -> DadosContato.de("11999990000", " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
