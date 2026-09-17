package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;

/**
 * Porta de entrada — recebe o dado de atraso de fatura (já informado, sem integração real com
 * Billing — plan.md §0.1) e aciona a avaliação de inadimplência do Cliente (spec.md RF-002).
 */
public interface AtualizarFaturaClienteUseCase {

    Cliente atualizarFatura(ClienteId id, int diasAtrasoFaturaMaisAntiga);
}
