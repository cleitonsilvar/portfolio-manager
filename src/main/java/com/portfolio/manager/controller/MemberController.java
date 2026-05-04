package com.portfolio.manager.controller;

import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import com.portfolio.manager.service.ExternalMemberClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Gerenciamento de membros (via API externa mockada)")
@SecurityRequirement(name = "basicAuth")
public class MemberController {

    private final ExternalMemberClient externalMemberClient;

    @PostMapping
    public ResponseEntity<MemberResponseDTO> criar(@Valid @RequestBody MemberRequestDTO dto) {
        MemberResponseDTO created = externalMemberClient.criarMembro(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> listarTodos() {
        return ResponseEntity.ok(externalMemberClient.listarMembros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(externalMemberClient.buscarMembro(id));
    }
}
