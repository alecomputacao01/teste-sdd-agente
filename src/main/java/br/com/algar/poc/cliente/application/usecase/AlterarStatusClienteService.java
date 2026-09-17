package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.AcaoTransicaoStatus;
import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.ports.in.AlterarStatusClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class AlterarStatusClienteService implements AlterarStatusClienteUseCase {

    private final ClienteRepository repository;

    public AlterarStatusClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente alterarStatus(ClienteId id, AcaoTransicaoStatus acao) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        switch (acao) {
            case SUSPENDER_A_PEDIDO -> cliente.suspenderAPedido();
            case REATIVAR -> cliente.reativar();
            case CANCELAR -> cliente.cancelar();
        }
        return repository.save(cliente);
    }
}
