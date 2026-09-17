package br.com.algar.poc.cliente.domain.model;

import java.util.List;

/**
 * Perfil consolidado de um Cliente (spec.md RF-011) — documento/telefone já vêm mascarados pela
 * camada de aplicação quando o perfil do consumidor é CRM (RF-013); por isso são texto simples,
 * e não {@link Documento}/{@link DadosContato} (cujas invariantes de formato uma máscara quebraria).
 */
public record Perfil360(
        String documento,
        String telefone,
        String email,
        List<Endereco> enderecos,
        List<ContaFaturamento> contasFaturamento,
        List<Contrato> contratos) {
}
