package com.portfolio.manager.controller;

import com.portfolio.manager.dto.request.ProjectFilterDTO;
import com.portfolio.manager.dto.request.ProjectRequestDTO;
import com.portfolio.manager.dto.request.StatusUpdateRequestDTO;
import com.portfolio.manager.dto.response.ProjectResponseDTO;
import com.portfolio.manager.service.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gerenciamento de projetos")
@SecurityRequirement(name = "basicAuth")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> criar(@Valid @RequestBody ProjectRequestDTO dto) {
        ProjectResponseDTO created = projectService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponseDTO>> listar(
        ProjectFilterDTO filter,
        @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(projectService.listar(filter, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody ProjectRequestDTO dto) {
        return ResponseEntity.ok(projectService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        projectService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponseDTO> atualizarStatus(
        @PathVariable Long id,
        @Valid @RequestBody StatusUpdateRequestDTO dto) {
        return ResponseEntity.ok(projectService.atualizarStatus(id, dto.getStatus()));
    }

    @PostMapping("/{id}/membros/{membroId}")
    public ResponseEntity<ProjectResponseDTO> adicionarMembro(
        @PathVariable Long id,
        @PathVariable Long membroId) {
        return ResponseEntity.ok(projectService.adicionarMembro(id, membroId));
    }

    @DeleteMapping("/{id}/membros/{membroId}")
    public ResponseEntity<Void> removerMembro(
        @PathVariable Long id,
        @PathVariable Long membroId) {
        projectService.removerMembro(id, membroId);
        return ResponseEntity.noContent().build();
    }
}
