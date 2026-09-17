package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.EstadoCliente;

/** Porta de entrada — consulta status atual e ações disponíveis (spec.md RF-004). */
public interface ConsultarEstadoClienteUseCase {

    EstadoCliente consultar(ClienteId id);
}
