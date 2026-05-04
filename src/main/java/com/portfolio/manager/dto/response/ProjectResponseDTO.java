package com.portfolio.manager.dto.response;

import com.portfolio.manager.domain.enums.ProjectStatus;
import com.portfolio.manager.domain.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {

    private Long id;
    private String nome;
    private LocalDate dataInicio;
    private LocalDate previsaoTermino;
    private LocalDate dataRealTermino;
    private BigDecimal orcamentoTotal;
    private String descricao;
    private ProjectStatus status;
    private RiskLevel risco;
    private MemberResponseDTO gerente;
    private List<MemberResponseDTO> membros;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
