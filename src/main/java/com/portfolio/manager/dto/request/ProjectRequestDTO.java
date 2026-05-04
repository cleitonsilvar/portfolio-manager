package com.portfolio.manager.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotNull
    private LocalDate dataInicio;

    @NotNull
    private LocalDate previsaoTermino;

    private LocalDate dataRealTermino;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal orcamentoTotal;

    private String descricao;
    private Long gerenteId;
}
