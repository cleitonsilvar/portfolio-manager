package com.portfolio.manager.service;

import com.portfolio.manager.domain.entity.Member;
import com.portfolio.manager.domain.repository.MemberRepository;
import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public MemberResponseDTO criar(MemberRequestDTO dto) {
        Member member = memberMapper.toEntity(dto);
        return memberMapper.toDTO(memberRepository.save(member));
    }

    public MemberResponseDTO buscarPorId(Long id) {
        return memberMapper.toDTO(
            memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro", id))
        );
    }

    public List<MemberResponseDTO> listarTodos() {
        return memberMapper.toDTOList(memberRepository.findAll());
    }

    public List<MemberResponseDTO> listarFuncionarios() {
        return memberMapper.toDTOList(memberRepository.findByAtribuicaoIgnoreCase("funcionario"));
    }

    public Member buscarEntidadePorId(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Membro", id));
    }
}
