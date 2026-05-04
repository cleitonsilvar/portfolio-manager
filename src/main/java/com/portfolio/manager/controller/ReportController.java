package com.portfolio.manager.controller;

import com.portfolio.manager.dto.response.PortfolioReportDTO;
import com.portfolio.manager.service.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio Report", description = "Relatório do portfólio de projetos")
@SecurityRequirement(name = "basicAuth")
public class ReportController {

    private final ProjectService projectService;

    @GetMapping("/report")
    public ResponseEntity<PortfolioReportDTO> gerarRelatorio() {
        return ResponseEntity.ok(projectService.gerarRelatorio());
    }
}
