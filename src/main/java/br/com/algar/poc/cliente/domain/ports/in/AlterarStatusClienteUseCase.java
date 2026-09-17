package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.AcaoTransicaoStatus;
import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;

/** Porta de entrada — aciona suspensão a pedido, reativação ou cancelamento (spec.md RF-001/RF-003). */
public interface AlterarStatusClienteUseCase {

    Cliente alterarStatus(ClienteId id, AcaoTransicaoStatus acao);
}
