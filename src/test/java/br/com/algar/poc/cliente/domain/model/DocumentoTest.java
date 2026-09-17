package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** TDD: escrito antes de {@link Documento} existir (tasks.md T007). Cobre RF-008. */
class DocumentoTest {

    @Test
    void aceitaCpfComOnzeDigitos() {
        var documento = Documento.de("529.982.247-25");

        assertThat(documento.value()).isEqualTo("52998224725");
    }

    @Test
    void aceitaCnpjComQuatorzeDigitos() {
        var documento = Documento.de("11.222.333/0001-81");

        assertThat(documento.value()).isEqualTo("11222333000181");
    }

    @Test
    void rejeitaDocumentoComQuantidadeDeDigitosInvalida() {
        assertThatThrownBy(() -> Documento.de("123"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doisDocumentosComOMesmoValorSaoIguais() {
        assertThat(Documento.de("52998224725")).isEqualTo(Documento.de("529.982.247-25"));
    }
}
