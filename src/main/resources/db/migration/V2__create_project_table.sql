CREATE TABLE project (
    id                  BIGSERIAL PRIMARY KEY,
    nome                VARCHAR(255) NOT NULL,
    data_inicio         DATE NOT NULL,
    previsao_termino    DATE NOT NULL,
    data_real_termino   DATE,
    orcamento_total     NUMERIC(15, 2) NOT NULL,
    descricao           TEXT,
    status              VARCHAR(50) NOT NULL DEFAULT 'EM_ANALISE',
    gerente_id          BIGINT REFERENCES member(id),
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);
