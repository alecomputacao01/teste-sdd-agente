package br.com.algar.poc.cliente.domain.model;

import java.util.List;
import java.util.Objects;

/** Value Object — resultado da verificação de elegibilidade a uma oferta de alto valor (spec.md RF-007). */
public final class ResultadoElegibilidade {

    private final boolean elegivel;
    private final List<String> motivos;

    private ResultadoElegibilidade(boolean elegivel, List<String> motivos) {
        this.elegivel = elegivel;
        this.motivos = motivos;
    }

    public static ResultadoElegibilidade criarElegivel() {
        return new ResultadoElegibilidade(true, List.of());
    }

    public static ResultadoElegibilidade criarNaoElegivel(List<String> motivos) {
        Objects.requireNonNull(motivos, "motivos não pode ser nulo");
        if (motivos.isEmpty()) {
            throw new IllegalArgumentException("recusa deve ter ao menos um motivo");
        }
        return new ResultadoElegibilidade(false, List.copyOf(motivos));
    }

    public boolean elegivel() {
        return elegivel;
    }

    public List<String> motivos() {
        return motivos;
    }
}
