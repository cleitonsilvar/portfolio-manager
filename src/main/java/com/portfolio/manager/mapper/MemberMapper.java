package com.portfolio.manager.mapper;

import com.portfolio.manager.domain.entity.Member;
import com.portfolio.manager.dto.request.MemberRequestDTO;
import com.portfolio.manager.dto.response.MemberResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponseDTO toDTO(Member member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "projetos", ignore = true)
    Member toEntity(MemberRequestDTO dto);

    List<MemberResponseDTO> toDTOList(List<Member> members);
}
