package com.portfolio.manager.domain.entity;

import com.portfolio.manager.domain.enums.ProjectStatus;
import com.portfolio.manager.domain.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "previsao_termino", nullable = false)
    private LocalDate previsaoTermino;

    @Column(name = "data_real_termino")
    private LocalDate dataRealTermino;

    @Column(name = "orcamento_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal orcamentoTotal;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.EM_ANALISE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id")
    private Member gerente;

    @ManyToMany
    @JoinTable(
        name = "projeto_membro",
        joinColumns = @JoinColumn(name = "projeto_id"),
        inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    @Builder.Default
    private List<Member> membros = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public RiskLevel calcularRisco() {
        long months = ChronoUnit.MONTHS.between(dataInicio, previsaoTermino);
        boolean altoBudget = orcamentoTotal.compareTo(new BigDecimal("500000")) > 0;
        boolean medioBudget = orcamentoTotal.compareTo(new BigDecimal("100000")) > 0;

        if (altoBudget || months > 6) {
            return RiskLevel.ALTO;
        } else if (medioBudget || months > 3) {
            return RiskLevel.MEDIO;
        }
        return RiskLevel.BAIXO;
    }
}
