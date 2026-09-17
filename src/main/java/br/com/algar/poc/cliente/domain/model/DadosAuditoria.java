package br.com.algar.poc.cliente.domain.model;

import java.util.List;

/**
 * Dados completos do Cliente para a rota restrita de Auditoria/Fraude, sem máscara (spec.md
 * RF-013). Não inclui senha de roteador, token de autenticação nem dado parcial de pagamento
 * (RF-012) — esses conceitos não existem no modelo (ver ClienteTest#naoExpoeDadosSensiveisNaSuperficiePublica).
 */
public record DadosAuditoria(
        String documento,
        String telefone,
        String email,
        StatusCliente status,
        List<Endereco> enderecos,
        List<ContaFaturamento> contasFaturamento,
        List<Contrato> contratos) {
}
