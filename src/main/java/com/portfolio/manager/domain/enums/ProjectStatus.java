package com.portfolio.manager.domain.enums;

import java.util.List;

public enum ProjectStatus {

    EM_ANALISE("Em Análise"),
    ANALISE_REALIZADA("Análise Realizada"),
    ANALISE_APROVADA("Análise Aprovada"),
    INICIADO("Iniciado"),
    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em Andamento"),
    ENCERRADO("Encerrado"),
    CANCELADO("Cancelado");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static List<ProjectStatus> getNext(ProjectStatus current) {
        return switch (current) {
            case EM_ANALISE      -> List.of(ANALISE_REALIZADA, CANCELADO);
            case ANALISE_REALIZADA -> List.of(ANALISE_APROVADA, CANCELADO);
            case ANALISE_APROVADA  -> List.of(INICIADO, CANCELADO);
            case INICIADO        -> List.of(PLANEJADO, CANCELADO);
            case PLANEJADO       -> List.of(EM_ANDAMENTO, CANCELADO);
            case EM_ANDAMENTO    -> List.of(ENCERRADO, CANCELADO);
            case ENCERRADO, CANCELADO -> List.of();
        };
    }
}
