package br.com.algar.poc.cliente.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosContatoRequest(
        @NotBlank String telefone,
        @NotBlank String email
) {
}
