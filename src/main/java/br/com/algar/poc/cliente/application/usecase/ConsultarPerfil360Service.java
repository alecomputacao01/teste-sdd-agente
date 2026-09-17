package br.com.algar.poc.cliente.application.usecase;

import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.Perfil360;
import br.com.algar.poc.cliente.domain.model.PerfilConsumidor;
import br.com.algar.poc.cliente.domain.ports.in.ConsultarPerfil360UseCase;
import br.com.algar.poc.cliente.domain.ports.out.ClienteRepository;

import java.util.NoSuchElementException;

/**
 * RF-013 — mascara documento/telefone quando o perfil do consumidor é CRM. O mascaramento vive aqui
 * (não no domínio) porque o resultado deixa de ser um {@code Documento}/{@code DadosContato} válido
 * (plan.md, ver Perfil360).
 */
public class ConsultarPerfil360Service implements ConsultarPerfil360UseCase {

    private final ClienteRepository repository;

    public ConsultarPerfil360Service(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Perfil360 consultar(ClienteId id, PerfilConsumidor perfil) {
        var cliente = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("cliente não encontrado: " + id));
        boolean mascarar = perfil == PerfilConsumidor.CRM;
        var documento = mascarar ? mascarar(cliente.documento().value()) : cliente.documento().value();
        var telefone = mascarar ? mascarar(cliente.dadosContato().telefone()) : cliente.dadosContato().telefone();
        return new Perfil360(documento, telefone, cliente.dadosContato().email(),
                cliente.enderecos(), cliente.contasFaturamento(), cliente.contratos());
    }

    private static String mascarar(String valor) {
        if (valor.length() <= 2) {
            return "*".repeat(valor.length());
        }
        return "*".repeat(valor.length() - 2) + valor.substring(valor.length() - 2);
    }
}
