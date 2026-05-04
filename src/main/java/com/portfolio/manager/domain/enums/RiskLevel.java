package com.portfolio.manager.domain.enums;

public enum RiskLevel {

    BAIXO("Baixo"),
    MEDIO("Médio"),
    ALTO("Alto");

    private final String label;

    RiskLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
