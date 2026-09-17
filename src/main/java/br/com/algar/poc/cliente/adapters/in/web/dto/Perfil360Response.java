package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.Contrato;
import br.com.algar.poc.cliente.domain.model.Perfil360;

import java.util.List;

public record Perfil360Response(
        String documento,
        String telefone,
        String email,
        List<EnderecoResponse> enderecos,
        List<ContaFaturamentoResponse> contasFaturamento,
        List<Contrato> contratos) {

    public static Perfil360Response from(Perfil360 perfil) {
        return new Perfil360Response(
                perfil.documento(),
                perfil.telefone(),
                perfil.email(),
                perfil.enderecos().stream().map(EnderecoResponse::from).toList(),
                perfil.contasFaturamento().stream().map(ContaFaturamentoResponse::from).toList(),
                perfil.contratos());
    }
}
