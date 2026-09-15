package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Product;
import br.com.algar.poc.cliente.domain.model.Sku;
import br.com.algar.poc.cliente.domain.ports.out.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TDD: escrito antes de {@link RegisterProductService} existir. Unitário puro — repositório mockado,
 * sem contexto Spring (é a camada "application", que orquestra o domínio via portas — plan.md §4).
 */
@ExtendWith(MockitoExtension.class)
class RegisterProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Test
    void deveRegistrarProdutoQuandoSkuNaoExiste() {
        when(repository.existsBySku(Sku.of("ABC-1234"))).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var service = new RegisterProductService(repository);
        var product = service.register("ABC-1234", "Notebook", new BigDecimal("100.00"));

        assertThat(product.sku()).isEqualTo(Sku.of("ABC-1234"));
        verify(repository).save(any(Product.class));
    }

    @Test
    void naoDeveRegistrarProdutoComSkuJaExistente() {
        when(repository.existsBySku(Sku.of("ABC-1234"))).thenReturn(true);

        var service = new RegisterProductService(repository);

        assertThatThrownBy(() -> service.register("ABC-1234", "Notebook", new BigDecimal("100.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SKU");
    }
}
