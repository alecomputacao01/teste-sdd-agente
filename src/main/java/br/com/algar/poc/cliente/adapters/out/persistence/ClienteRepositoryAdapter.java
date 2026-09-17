package br.com.algar.poc.cliente.adapters.out.persistence;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        var saved = jpaRepository.save(ClienteMapper.toEntity(cliente));
        return ClienteMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(ClienteId id) {
        return jpaRepository.findById(id.value()).map(ClienteMapper::toDomain);
    }
}
