package com.portfolio.manager.dto.request;

import com.portfolio.manager.domain.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequestDTO {

    @NotNull
    private ProjectStatus status;
}
