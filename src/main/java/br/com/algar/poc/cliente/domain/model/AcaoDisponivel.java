package br.com.algar.poc.cliente.domain.model;

/** Ações que CRM/Portal podem oferecer para um Cliente, conforme seu status atual (spec.md RF-004). */
public enum AcaoDisponivel {
    SUSPENDER_A_PEDIDO,
    REATIVAR,
    ALTERAR,
    CANCELAR
}
