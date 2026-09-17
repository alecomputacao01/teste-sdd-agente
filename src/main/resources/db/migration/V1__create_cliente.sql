CREATE TABLE cliente (
    id UUID PRIMARY KEY,
    documento VARCHAR(14) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    score_credito VARCHAR(10),
    possui_contestacao_aberta BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE cliente_endereco (
    cliente_id UUID NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    endereco_ordem INTEGER NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    principal BOOLEAN NOT NULL,
    validado_nos_correios BOOLEAN NOT NULL,
    PRIMARY KEY (cliente_id, endereco_ordem)
);

CREATE TABLE conta_faturamento (
    id UUID PRIMARY KEY,
    cliente_id UUID REFERENCES cliente(id) ON DELETE CASCADE,
    descricao VARCHAR(255) NOT NULL,
    endereco_logradouro VARCHAR(255) NOT NULL,
    endereco_numero VARCHAR(20) NOT NULL,
    endereco_cep VARCHAR(10) NOT NULL,
    endereco_cidade VARCHAR(255) NOT NULL,
    endereco_uf VARCHAR(2) NOT NULL,
    endereco_principal BOOLEAN NOT NULL,
    endereco_validado_nos_correios BOOLEAN NOT NULL
);

CREATE TABLE cliente_contrato (
    cliente_id UUID NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    contrato_ordem INTEGER NOT NULL,
    contrato_id VARCHAR(50) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (cliente_id, contrato_ordem)
);
