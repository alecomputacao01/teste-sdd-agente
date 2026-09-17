package br.com.algar.poc.cliente.domain.ports.in;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Endereco;

import java.util.List;

/** Porta de entrada (driving port) — cadastro de um novo Cliente (spec.md RF-008/RF-009/RF-010). */
public interface CadastrarClienteUseCase {

    Cliente cadastrar(String documento, List<Endereco> enderecos, DadosContato dadosContato);
}
