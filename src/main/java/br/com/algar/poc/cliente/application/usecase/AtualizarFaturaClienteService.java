package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarFaturaClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class AtualizarFaturaClienteService implements AtualizarFaturaClienteUseCase {

    private final ClienteRepository repository;

    public AtualizarFaturaClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente atualizarFatura(ClienteId id, int diasAtrasoFaturaMaisAntiga) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        cliente.avaliarAtrasoFatura(diasAtrasoFaturaMaisAntiga);
        return repository.save(cliente);
    }
}
