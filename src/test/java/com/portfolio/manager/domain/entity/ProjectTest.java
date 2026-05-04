package com.portfolio.manager.domain.entity;

import com.portfolio.manager.domain.enums.RiskLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectTest {

    private Project buildProject(String orcamento, LocalDate inicio, LocalDate termino) {
        return Project.builder()
            .nome("Test")
            .orcamentoTotal(new BigDecimal(orcamento))
            .dataInicio(inicio)
            .previsaoTermino(termino)
            .build();
    }

    @Test
    void calcularRisco_orcamento50k_prazo2meses_retornaBaixo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("50000", inicio, inicio.plusMonths(2));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    void calcularRisco_orcamento300k_prazo4meses_retornaMedio() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("300000", inicio, inicio.plusMonths(4));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void calcularRisco_orcamento50k_prazo5meses_retornaMedio() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("50000", inicio, inicio.plusMonths(5));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void calcularRisco_orcamento600k_prazo1mes_retornaAlto() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("600000", inicio, inicio.plusMonths(1));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void calcularRisco_orcamento200k_prazo8meses_retornaAlto() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("200000", inicio, inicio.plusMonths(8));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void calcularRisco_fronteira_orcamento100k_prazo3meses_retornaBaixo() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("100000", inicio, inicio.plusMonths(3));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    void calcularRisco_fronteira_orcamento100001_prazo1mes_retornaMedio() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("100001", inicio, inicio.plusMonths(1));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void calcularRisco_orcamento500001_retornaAlto() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("500001", inicio, inicio.plusMonths(1));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void calcularRisco_prazo6meses_orcamento50k_retornaMedio() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("50000", inicio, inicio.plusMonths(6));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void calcularRisco_prazo7meses_orcamento50k_retornaAlto() {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        Project p = buildProject("50000", inicio, inicio.plusMonths(7));
        assertThat(p.calcularRisco()).isEqualTo(RiskLevel.ALTO);
    }
}
