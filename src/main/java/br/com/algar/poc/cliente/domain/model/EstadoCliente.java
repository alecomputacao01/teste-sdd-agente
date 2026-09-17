package br.com.algar.poc.cliente.domain.model;

import java.util.Set;

/** Value Object — status atual e ações disponíveis para o CRM/Portal renderizarem (spec.md RF-004). */
public record EstadoCliente(StatusCliente status, Set<AcaoDisponivel> acoesDisponiveis) {
}
