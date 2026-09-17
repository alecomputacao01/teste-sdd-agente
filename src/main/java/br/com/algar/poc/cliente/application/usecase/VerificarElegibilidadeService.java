package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ResultadoElegibilidade;
import br.com.algar.poc.cliente.domain.ports.in.VerificarElegibilidadeUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class VerificarElegibilidadeService implements VerificarElegibilidadeUseCase {

    private final ClienteRepository repository;

    public VerificarElegibilidadeService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResultadoElegibilidade verificar(ClienteId id) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        return cliente.verificarElegibilidade();
    }
}
