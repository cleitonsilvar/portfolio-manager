package com.portfolio.manager.service;

import com.portfolio.manager.domain.entity.Member;
import com.portfolio.manager.domain.repository.MemberRepository;
import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.MemberMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private MemberMapper memberMapper;
    @InjectMocks private MemberService memberService;

    @Test
    void buscarPorId_idInexistente_lancaResourceNotFoundException() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.buscarPorId(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void buscarPorId_idExistente_retornaMembro() {
        Member member = Member.builder().id(1L).nome("Ana").atribuicao("funcionario").build();
        MemberResponseDTO dto = new MemberResponseDTO(1L, "Ana", "funcionario");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberMapper.toDTO(member)).thenReturn(dto);

        MemberResponseDTO result = memberService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Ana");
    }

    @Test
    void criar_membroValido_salvaeRetornaDTO() {
        MemberRequestDTO req = new MemberRequestDTO("Carlos", "funcionario");
        Member entity = Member.builder().id(3L).nome("Carlos").atribuicao("funcionario").build();
        MemberResponseDTO dto = new MemberResponseDTO(3L, "Carlos", "funcionario");

        when(memberMapper.toEntity(req)).thenReturn(entity);
        when(memberRepository.save(entity)).thenReturn(entity);
        when(memberMapper.toDTO(entity)).thenReturn(dto);

        MemberResponseDTO result = memberService.criar(req);

        assertThat(result.getNome()).isEqualTo("Carlos");
    }

    @Test
    void listarTodos_retornaListaCompleta() {
        Member m1 = Member.builder().id(1L).nome("Ana").atribuicao("funcionario").build();
        Member m2 = Member.builder().id(2L).nome("Bob").atribuicao("gerente").build();
        MemberResponseDTO d1 = new MemberResponseDTO(1L, "Ana", "funcionario");
        MemberResponseDTO d2 = new MemberResponseDTO(2L, "Bob", "gerente");

        when(memberRepository.findAll()).thenReturn(List.of(m1, m2));
        when(memberMapper.toDTOList(any())).thenReturn(List.of(d1, d2));

        List<MemberResponseDTO> result = memberService.listarTodos();

        assertThat(result).hasSize(2);
    }

    @Test
    void listarFuncionarios_retornaApenasComAtribuicaoFuncionario() {
        Member funcionario = Member.builder().id(1L).nome("Ana").atribuicao("funcionario").build();
        MemberResponseDTO dto = new MemberResponseDTO(1L, "Ana", "funcionario");

        when(memberRepository.findByAtribuicaoIgnoreCase("funcionario")).thenReturn(List.of(funcionario));
        when(memberMapper.toDTOList(List.of(funcionario))).thenReturn(List.of(dto));

        List<MemberResponseDTO> result = memberService.listarFuncionarios();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAtribuicao()).isEqualTo("funcionario");
    }
}
