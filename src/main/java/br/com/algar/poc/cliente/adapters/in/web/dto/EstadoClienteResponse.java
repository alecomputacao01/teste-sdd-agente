package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.AcaoDisponivel;
import br.com.algar.poc.cliente.domain.model.EstadoCliente;
import br.com.algar.poc.cliente.domain.model.StatusCliente;

import java.util.Set;

public record EstadoClienteResponse(StatusCliente status, Set<AcaoDisponivel> acoesDisponiveis) {

    public static EstadoClienteResponse from(EstadoCliente estado) {
        return new EstadoClienteResponse(estado.status(), estado.acoesDisponiveis());
    }
}
