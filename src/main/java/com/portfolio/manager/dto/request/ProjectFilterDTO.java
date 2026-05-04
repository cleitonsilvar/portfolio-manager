package com.portfolio.manager.dto.request;

import com.portfolio.manager.domain.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDTO {

    private String nome;
    private ProjectStatus status;
    private Long gerenteId;
}
