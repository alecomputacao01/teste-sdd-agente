package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.AcaoTransicaoStatus;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusClienteRequest(@NotNull AcaoTransicaoStatus acao) {
}
