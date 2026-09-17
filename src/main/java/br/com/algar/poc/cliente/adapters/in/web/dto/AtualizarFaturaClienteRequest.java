package br.com.algar.poc.cliente.adapters.in.web.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record AtualizarFaturaClienteRequest(@PositiveOrZero int diasAtrasoFaturaMaisAntiga) {
}
