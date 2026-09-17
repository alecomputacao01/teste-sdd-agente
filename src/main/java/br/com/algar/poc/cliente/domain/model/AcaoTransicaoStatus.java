package br.com.algar.poc.cliente.domain.model;

/** Ações de transição de status que o CRM/Portal podem acionar (spec.md RF-001, RF-003). */
public enum AcaoTransicaoStatus {
    SUSPENDER_A_PEDIDO,
    REATIVAR,
    CANCELAR
}
