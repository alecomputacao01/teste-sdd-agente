package br.com.algar.poc.cliente.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record EnderecoRequest(
        @NotBlank String logradouro,
        @NotBlank String numero,
        @NotBlank String cep,
        @NotBlank String cidade,
        @NotBlank String uf,
        boolean principal,
        boolean validadoNosCorreios
) {
}
