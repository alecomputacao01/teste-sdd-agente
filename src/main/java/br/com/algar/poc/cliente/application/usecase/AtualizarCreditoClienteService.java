package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ScoreCredito;
import br.com.algar.poc.cliente.domain.ports.in.AtualizarCreditoClienteUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class AtualizarCreditoClienteService implements AtualizarCreditoClienteUseCase {

    private final ClienteRepository repository;

    public AtualizarCreditoClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente atualizarCredito(ClienteId id, ScoreCredito scoreCredito, boolean possuiContestacaoAbertaUltimos6Meses) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        cliente.atualizarDadosCredito(scoreCredito, possuiContestacaoAbertaUltimos6Meses);
        return repository.save(cliente);
    }
}
