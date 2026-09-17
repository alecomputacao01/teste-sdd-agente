package br.com.algar.poc.cliente.adapters.out.persistence;

import br.com.algar.poc.cliente.domain.model.Cliente;
import br.com.algar.poc.cliente.domain.model.ClienteId;
import br.com.algar.poc.cliente.domain.model.ContaFaturamento;
import br.com.algar.poc.cliente.domain.model.ContaFaturamentoId;
import br.com.algar.poc.cliente.domain.model.Contrato;
import br.com.algar.poc.cliente.domain.model.DadosContato;
import br.com.algar.poc.cliente.domain.model.Documento;
import br.com.algar.poc.cliente.domain.model.Endereco;
import br.com.algar.poc.cliente.domain.model.ScoreCredito;
import br.com.algar.poc.cliente.domain.model.StatusCliente;

final class ClienteMapper {

    private ClienteMapper() {
    }

    static ClienteJpaEntity toEntity(Cliente cliente) {
        var enderecos = cliente.enderecos().stream().map(ClienteMapper::toEmbeddable).toList();
        var contasFaturamento = cliente.contasFaturamento().stream().map(ClienteMapper::toEntity).toList();
        var contratos = cliente.contratos().stream()
                .map(c -> new ContratoEmbeddable(c.id(), c.descricao(), c.status()))
                .toList();
        return new ClienteJpaEntity(
                cliente.id().value(),
                cliente.documento().value(),
                cliente.status().name(),
                cliente.dadosContato().telefone(),
                cliente.dadosContato().email(),
                cliente.scoreCredito() == null ? null : cliente.scoreCredito().name(),
                cliente.possuiContestacaoAbertaUltimos6Meses(),
                enderecos,
                contasFaturamento,
                contratos);
    }

    static Cliente toDomain(ClienteJpaEntity entity) {
        var enderecos = entity.getEnderecos().stream().map(ClienteMapper::toDomain).toList();
        var contasFaturamento = entity.getContasFaturamento().stream().map(ClienteMapper::toDomain).toList();
        var contratos = entity.getContratos().stream()
                .map(c -> new Contrato(c.getContratoId(), c.getDescricao(), c.getStatus()))
                .toList();
        var scoreCredito = entity.getScoreCredito() == null ? null : ScoreCredito.valueOf(entity.getScoreCredito());
        return Cliente.reconstituir(
                ClienteId.de(entity.getId()),
                Documento.de(entity.getDocumento()),
                StatusCliente.valueOf(entity.getStatus()),
                DadosContato.de(entity.getTelefone(), entity.getEmail()),
                enderecos,
                contasFaturamento,
                contratos,
                scoreCredito,
                entity.isPossuiContestacaoAbertaUltimos6Meses());
    }

    private static EnderecoEmbeddable toEmbeddable(Endereco endereco) {
        return new EnderecoEmbeddable(endereco.logradouro(), endereco.numero(), endereco.cep(),
                endereco.cidade(), endereco.uf(), endereco.principal(), endereco.validadoNosCorreios());
    }

    private static Endereco toDomain(EnderecoEmbeddable embeddable) {
        return Endereco.de(embeddable.getLogradouro(), embeddable.getNumero(), embeddable.getCep(),
                embeddable.getCidade(), embeddable.getUf(), embeddable.isPrincipal(), embeddable.isValidadoNosCorreios());
    }

    private static ContaFaturamentoJpaEntity toEntity(ContaFaturamento conta) {
        return new ContaFaturamentoJpaEntity(conta.id().value(), conta.descricao(), toEmbeddable(conta.enderecoDeCobranca()));
    }

    private static ContaFaturamento toDomain(ContaFaturamentoJpaEntity entity) {
        return ContaFaturamento.criar(ContaFaturamentoId.de(entity.getId()), entity.getDescricao(),
                toDomain(entity.getEnderecoDeCobranca()));
    }
}
