package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.Endereco;

public record EnderecoResponse(String logradouro, String numero, String cep, String cidade, String uf, boolean principal) {

    public static EnderecoResponse from(Endereco endereco) {
        return new EnderecoResponse(endereco.logradouro(), endereco.numero(), endereco.cep(),
                endereco.cidade(), endereco.uf(), endereco.principal());
    }
}
