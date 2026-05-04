package com.portfolio.manager.controller;

import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import com.portfolio.manager.service.MemberService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/external/members")
@RequiredArgsConstructor
@Tag(name = "External Members API (Mock)", description = "Simulação de API externa de membros")
@Hidden
public class ExternalMemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponseDTO> criar(@Valid @RequestBody MemberRequestDTO dto) {
        MemberResponseDTO created = memberService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> listarTodos() {
        return ResponseEntity.ok(memberService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.buscarPorId(id));
    }
}
