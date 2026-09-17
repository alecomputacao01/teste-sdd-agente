package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.Cliente;

public record ClienteResponse(String id, String status) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.id().toString(), cliente.status().name());
    }
}
