package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ResultadoElegibilidade;

/** Porta de entrada — verifica elegibilidade a oferta de alto valor (spec.md RF-005/RF-006/RF-007). */
public interface VerificarElegibilidadeUseCase {

    ResultadoElegibilidade verificar(ClienteId id);
}
