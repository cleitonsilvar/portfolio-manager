package com.portfolio.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {

    private Long id;
    private String nome;
    private String atribuicao;
}
