package com.portfolio.manager.mapper;

import com.portfolio.manager.domain.entity.Project;
import com.portfolio.manager.dto.request.ProjectRequestDTO;
import com.portfolio.manager.dto.response.ProjectResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MemberMapper.class})
public interface ProjectMapper {

    @Mapping(target = "risco", expression = "java(project.calcularRisco())")
    ProjectResponseDTO toDTO(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "gerente", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project toEntity(ProjectRequestDTO dto);

    List<ProjectResponseDTO> toDTOList(List<Project> projects);
}
