package br.com.algar.poc.cliente.adapters.in.web.dto;

import br.com.algar.poc.cliente.domain.model.ResultadoElegibilidade;

import java.util.List;

public record ElegibilidadeResponse(boolean elegivel, List<String> motivos) {

    public static ElegibilidadeResponse from(ResultadoElegibilidade resultado) {
        return new ElegibilidadeResponse(resultado.elegivel(), resultado.motivos());
    }
}
