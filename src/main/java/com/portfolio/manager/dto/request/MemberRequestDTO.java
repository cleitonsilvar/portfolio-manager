package com.portfolio.manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String atribuicao;
}
