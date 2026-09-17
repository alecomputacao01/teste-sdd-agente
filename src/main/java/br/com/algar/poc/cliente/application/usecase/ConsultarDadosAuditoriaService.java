package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.DadosAuditoria;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarDadosAuditoriaUseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

public class ConsultarDadosAuditoriaService implements ConsultarDadosAuditoriaUseCase {

    private final ClienteRepository repository;

    public ConsultarDadosAuditoriaService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public DadosAuditoria consultar(ClienteId id) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        return new DadosAuditoria(
                cliente.documento().value(),
                cliente.dadosContato().telefone(),
                cliente.dadosContato().email(),
                cliente.status(),
                cliente.enderecos(),
                cliente.contasFaturamento(),
                cliente.contratos());
    }
}
