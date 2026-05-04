package com.portfolio.manager.service;

import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalMemberClient {

    @Value("${external.member-api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public MemberResponseDTO criarMembro(MemberRequestDTO dto) {
        ResponseEntity<MemberResponseDTO> response = restTemplate.postForEntity(baseUrl, dto, MemberResponseDTO.class);
        return response.getBody();
    }

    public List<MemberResponseDTO> listarMembros() {
        ResponseEntity<List<MemberResponseDTO>> response = restTemplate.exchange(
            baseUrl, HttpMethod.GET, null,
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public MemberResponseDTO buscarMembro(Long id) {
        return restTemplate.getForObject(baseUrl + "/" + id, MemberResponseDTO.class);
    }
}
