package br.com.algar.poc.cliente.adapters.in.web;

import br.com.algar.poc.cliente.domain.model.Product;
import br.com.algar.poc.cliente.domain.model.ProductId;
import br.com.algar.poc.cliente.domain.model.Sku;
import br.com.algar.poc.cliente.domain.ports.in.RegisterProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TDD: escrito antes de {@link ProductController} existir.
 *
 * Nota de execução (achado já conhecido neste ambiente para Spring Boot 4.1): {@code @WebMvcTest}/
 * {@code @AutoConfigureMockMvc} não sobem contexto Spring utilizável nesta versão — usa-se MockMvc
 * standalone + Mockito, sem contexto, o que também mantém o teste rápido e sem depender de banco.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private RegisterProductUseCase registerProductUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(registerProductUseCase)).build();
    }

    @Test
    void deveRetornar201AoRegistrarProdutoValido() throws Exception {
        var product = Product.register(ProductId.newId(), Sku.of("ABC-1234"), "Notebook", new BigDecimal("100.00"));
        when(registerProductUseCase.register(eq("ABC-1234"), eq("Notebook"), any(BigDecimal.class)))
                .thenReturn(product);

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {"sku":"ABC-1234","name":"Notebook","price":100.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("ABC-1234"))
                .andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void deveRetornar409QuandoSkuJaExiste() throws Exception {
        when(registerProductUseCase.register(eq("ABC-1234"), eq("Notebook"), any(BigDecimal.class)))
                .thenThrow(new IllegalStateException("SKU já cadastrado: ABC-1234"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {"sku":"ABC-1234","name":"Notebook","price":100.00}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400QuandoRegraDeDominioForViolada() throws Exception {
        when(registerProductUseCase.register(eq("XX"), eq("Notebook"), any(BigDecimal.class)))
                .thenThrow(new IllegalArgumentException("SKU inválido"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("""
                                {"sku":"XX","name":"Notebook","price":100.00}
                                """))
                .andExpect(status().isBadRequest());
    }
}
