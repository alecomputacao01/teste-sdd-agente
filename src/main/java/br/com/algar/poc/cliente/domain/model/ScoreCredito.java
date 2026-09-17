package br.com.algar.poc.cliente.domain.model;

/**
 * Classificação interna de adimplência, já "limpa" pela camada de aplicação a partir do score de
 * crédito externo (spec.md RF-005) — o domínio nunca vê o dado bruto do sistema externo.
 */
public enum ScoreCredito {
    VERDE,
    AMARELO,
    VERMELHO
}
