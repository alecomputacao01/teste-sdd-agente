package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.Contrato;
import br.com.algar.poc.cliente.domain.model.DadosAuditoria;
import br.com.algar.poc.cliente.domain.model.StatusCliente;

import java.util.List;

/**
 * Sem máscara — rota restrita, criptografia ponta a ponta simulada nesta PoC (spec.md §6 NFR).
 * Não inclui senha de roteador, token de autenticação nem dado parcial de pagamento (RF-012).
 */
public record AuditoriaResponse(
        String documento,
        String telefone,
        String email,
        StatusCliente status,
        List<EnderecoResponse> enderecos,
        List<ContaFaturamentoResponse> contasFaturamento,
        List<Contrato> contratos) {

    public static AuditoriaResponse from(DadosAuditoria dados) {
        return new AuditoriaResponse(
                dados.documento(),
                dados.telefone(),
                dados.email(),
                dados.status(),
                dados.enderecos().stream().map(EnderecoResponse::from).toList(),
                dados.contasFaturamento().stream().map(ContaFaturamentoResponse::from).toList(),
                dados.contratos());
    }
}
