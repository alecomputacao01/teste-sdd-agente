package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosAuditoria;

/** Porta de entrada — dados completos sem máscara para a rota restrita de Auditoria/Fraude (spec.md RF-012/RF-013). */
public interface ConsultarDadosAuditoriaUseCase {

    DadosAuditoria consultar(ClienteId id);
}
