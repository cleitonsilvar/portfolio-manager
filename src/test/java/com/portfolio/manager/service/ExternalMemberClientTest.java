package com.portfolio.manager.service;

import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalMemberClientTest {

    @Mock private RestTemplate restTemplate;
    @InjectMocks private ExternalMemberClient client;

    private static final String BASE_URL = "http://localhost:8080/api/external/members";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "baseUrl", BASE_URL);
    }

    @Test
    void criarMembro_retornaMembroDTO() {
        MemberRequestDTO req = new MemberRequestDTO("Ana", "funcionario");
        MemberResponseDTO dto = new MemberResponseDTO(1L, "Ana", "funcionario");

        when(restTemplate.postForEntity(eq(BASE_URL), eq(req), eq(MemberResponseDTO.class)))
            .thenReturn(ResponseEntity.ok(dto));

        MemberResponseDTO result = client.criarMembro(req);

        assertThat(result.getNome()).isEqualTo("Ana");
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarMembros_retornaLista() {
        List<MemberResponseDTO> lista = List.of(
            new MemberResponseDTO(1L, "Ana", "funcionario")
        );

        when(restTemplate.exchange(
            eq(BASE_URL), eq(HttpMethod.GET), isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(lista));

        List<MemberResponseDTO> result = client.listarMembros();

        assertThat(result).hasSize(1);
    }

    @Test
    void buscarMembro_retornaMembroDTO() {
        MemberResponseDTO dto = new MemberResponseDTO(1L, "Ana", "funcionario");

        when(restTemplate.getForObject(BASE_URL + "/1", MemberResponseDTO.class))
            .thenReturn(dto);

        MemberResponseDTO result = client.buscarMembro(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }
}
