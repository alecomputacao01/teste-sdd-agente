package br.com.algar.poc.cliente.domain.ports.out;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;

import java.util.Optional;

/** Porta de saída (driven port) — persistência do Aggregate Cliente, sem detalhe de infraestrutura. */
public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(ClienteId id);
}
