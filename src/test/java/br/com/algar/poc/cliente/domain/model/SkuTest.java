package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkuTest {

    @Test
    void deveAceitarSkuNoFormatoValido() {
        var sku = Sku.of("ABC-1234");

        assertThat(sku.value()).isEqualTo("ABC-1234");
    }

    @Test
    void naoDeveAceitarSkuForaDoFormato() {
        assertThatThrownBy(() -> Sku.of("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU");

        assertThatThrownBy(() -> Sku.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU");

        assertThatThrownBy(() -> Sku.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SKU");
    }

    @Test
    void duasSkuComMesmoValorSaoIguais() {
        assertThat(Sku.of("ABC-1234")).isEqualTo(Sku.of("ABC-1234"));
    }
}
