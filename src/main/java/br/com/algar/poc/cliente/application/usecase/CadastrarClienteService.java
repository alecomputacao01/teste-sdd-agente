package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.ports.in.CadastrarClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.List;

/**
 * Implementa a porta de entrada, orquestrando domínio e porta de saída — não depende de nenhum
 * adapter concreto (regra fiscalizada por HexagonalArchitectureTest.application_nao_depende_de_adapters).
 */
public class CadastrarClienteService implements CadastrarClienteUseCase {

    private final ClienteRepository repository;

    public CadastrarClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente cadastrar(String documento, List<Endereco> enderecos, DadosContato dadosContato) {
        var cliente = Cliente.registrar(ClienteId.novo(), Documento.de(documento), enderecos, dadosContato);
        return repository.save(cliente);
    }
}
