package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.ScoreCredito;
import jakarta.validation.constraints.NotNull;

public record AtualizarCreditoClienteRequest(
        @NotNull ScoreCredito score,
        boolean possuiContestacaoAbertaUltimos6Meses
) {
}
