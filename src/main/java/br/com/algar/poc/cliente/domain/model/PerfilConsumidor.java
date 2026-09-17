package br.com.algar.poc.cliente.domain.model;

/**
 * Perfil do consumidor de {@code /perfil-360} (spec.md RF-013) — "dado de contexto recebido", não
 * mecanismo de autenticação (plan.md §0.5): seleciona a máscara aplicada, não autentica ninguém.
 */
public enum PerfilConsumidor {
    PORTAL,
    CRM
}
