package br.com.algar.poc.cliente.domain.model;

/** Estados do ciclo de vida do assinante (spec.md RF-001). */
public enum StatusCliente {
    ATIVO,
    SUSPENSO_INADIMPLENCIA,
    SUSPENSO_A_PEDIDO,
    CANCELADO
}
