package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.EstadoCliente;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarEstadoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class ConsultarEstadoClienteService implements ConsultarEstadoClienteUseCase {

    private final ClienteRepository repository;

    public ConsultarEstadoClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public EstadoCliente consultar(ClienteId id) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        return new EstadoCliente(cliente.status(), cliente.obterAcoesDisponiveis());
    }
}
