package br.com.algar.poc.cliente.domain.model;

/**
 * Referência somente-leitura a um contrato (plan.md §0.4) — usada só para compor o perfil-360
 * (spec.md RF-011). Criação/gestão real de contratos é de outro serviço, fora de escopo aqui.
 */
public record Contrato(String id, String descricao, String status) {
}
