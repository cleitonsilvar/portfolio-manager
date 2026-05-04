package com.portfolio.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioReportDTO {

    private Map<String, Long> quantidadePorStatus;
    private Map<String, BigDecimal> totalOrcadoPorStatus;
    private Double mediaDuracaoEncerradosEmDias;
    private Long totalMembrosUnicos;
}
