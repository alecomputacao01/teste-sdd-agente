package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.ContaFaturamento;

public record ContaFaturamentoResponse(String id, String descricao) {

    public static ContaFaturamentoResponse from(ContaFaturamento conta) {
        return new ContaFaturamentoResponse(conta.id().toString(), conta.descricao());
    }
}
