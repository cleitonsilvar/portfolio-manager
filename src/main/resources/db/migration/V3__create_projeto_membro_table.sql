CREATE TABLE projeto_membro (
    projeto_id  BIGINT NOT NULL REFERENCES project(id),
    membro_id   BIGINT NOT NULL REFERENCES member(id),
    PRIMARY KEY (projeto_id, membro_id)
);
