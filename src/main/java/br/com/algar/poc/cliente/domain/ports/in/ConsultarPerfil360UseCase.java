package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.Perfil360;
import br.com.algar.poc.cliente.domain.model.PerfilConsumidor;

/**
 * Porta de entrada — perfil consolidado (endereços, contas de faturamento, contratos), com
 * mascaramento de documento/telefone quando o perfil do consumidor é CRM (spec.md RF-011/RF-013).
 */
public interface ConsultarPerfil360UseCase {

    Perfil360 consultar(ClienteId id, PerfilConsumidor perfil);
}
