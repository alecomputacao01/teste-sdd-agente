package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ScoreCredito;

/**
 * Porta de entrada — recebe score interno e flag de contestação (já informados, sem integração real
 * com sistemas de crédito — plan.md §0.1) para uso posterior por {@link VerificarElegibilidadeUseCase}.
 */
public interface AtualizarCreditoClienteUseCase {

    Cliente atualizarCredito(ClienteId id, ScoreCredito scoreCredito, boolean possuiContestacaoAbertaUltimos6Meses);
}
