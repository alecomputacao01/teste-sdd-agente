package br.com.algar.poc.cliente.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TDD: estes testes foram escritos antes de {@link Product}, {@link ProductId} e {@link Sku} existirem.
 * Puros — sem Spring, sem framework — por serem a camada de domínio do estilo Hexagonal (plan.md secao 4).
 */
class ProductTest {

    @Test
    void deveRegistrarProdutoValido() {
        var id = ProductId.newId();
        var sku = Sku.of("ABC-1234");

        var product = Product.register(id, sku, "Notebook 14 polegadas", new BigDecimal("3599.90"));

        assertThat(product.id()).isEqualTo(id);
        assertThat(product.sku()).isEqualTo(sku);
        assertThat(product.name()).isEqualTo("Notebook 14 polegadas");
        assertThat(product.price()).isEqualByComparingTo("3599.90");
    }

    @Test
    void naoDeveRegistrarProdutoComPrecoZeroOuNegativo() {
        var id = ProductId.newId();
        var sku = Sku.of("ABC-1234");

        assertThatThrownBy(() -> Product.register(id, sku, "Mouse", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("preço");

        assertThatThrownBy(() -> Product.register(id, sku, "Mouse", new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("preço");
    }

    @Test
    void naoDeveRegistrarProdutoComNomeEmBranco() {
        var id = ProductId.newId();
        var sku = Sku.of("ABC-1234");

        assertThatThrownBy(() -> Product.register(id, sku, "   ", new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }

    @Test
    void deveRenomearProduto() {
        var product = Product.register(ProductId.newId(), Sku.of("ABC-1234"), "Nome antigo", new BigDecimal("10.00"));

        product.rename("Nome novo");

        assertThat(product.name()).isEqualTo("Nome novo");
    }

    @Test
    void naoDeveRenomearProdutoParaNomeEmBranco() {
        var product = Product.register(ProductId.newId(), Sku.of("ABC-1234"), "Nome antigo", new BigDecimal("10.00"));

        assertThatThrownBy(() -> product.rename(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nome");
    }
}
