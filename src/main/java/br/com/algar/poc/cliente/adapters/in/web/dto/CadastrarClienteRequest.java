package br.com.algar.poc.cliente.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CadastrarClienteRequest(
        @NotBlank String documento,
        @NotEmpty List<@Valid EnderecoRequest> enderecos,
        @NotNull @Valid DadosContatoRequest dadosContato
) {
}
