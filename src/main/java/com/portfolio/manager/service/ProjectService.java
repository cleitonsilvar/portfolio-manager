package com.portfolio.manager.service;

import com.portfolio.manager.domain.entity.Member;
import com.portfolio.manager.domain.entity.Project;
import com.portfolio.manager.domain.enums.ProjectStatus;
import com.portfolio.manager.domain.repository.MemberRepository;
import com.portfolio.manager.domain.repository.ProjectRepository;
import com.portfolio.manager.domain.repository.ProjectSpecification;
import com.portfolio.manager.dto.request.ProjectFilterDTO;
import com.portfolio.manager.dto.request.ProjectRequestDTO;
import com.portfolio.manager.dto.response.PortfolioReportDTO;
import com.portfolio.manager.dto.response.ProjectResponseDTO;
import com.portfolio.manager.exception.BusinessException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponseDTO criar(ProjectRequestDTO dto) {
        if (dto.getDataInicio().isAfter(dto.getPrevisaoTermino())) {
            throw new BusinessException("Data de início deve ser anterior à previsão de término", 422);
        }

        Project project = projectMapper.toEntity(dto);

        if (dto.getGerenteId() != null) {
            Member gerente = memberRepository.findById(dto.getGerenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Gerente", dto.getGerenteId()));
            project.setGerente(gerente);
        }

        return projectMapper.toDTO(projectRepository.save(project));
    }

    public ProjectResponseDTO buscarPorId(Long id) {
        return projectMapper.toDTO(findById(id));
    }

    public Page<ProjectResponseDTO> listar(ProjectFilterDTO filter, Pageable pageable) {
        Specification<Project> spec = Specification.where(null);

        if (filter != null && filter.getNome() != null && !filter.getNome().isBlank()) {
            spec = spec.and(ProjectSpecification.nomeContains(filter.getNome()));
        }
        if (filter != null && filter.getStatus() != null) {
            spec = spec.and(ProjectSpecification.statusEquals(filter.getStatus()));
        }
        if (filter != null && filter.getGerenteId() != null) {
            spec = spec.and(ProjectSpecification.gerenteIdEquals(filter.getGerenteId()));
        }

        return projectRepository.findAll(spec, pageable).map(projectMapper::toDTO);
    }

    @Transactional
    public ProjectResponseDTO atualizar(Long id, ProjectRequestDTO dto) {
        Project project = findById(id);

        if (project.getStatus() == ProjectStatus.ENCERRADO || project.getStatus() == ProjectStatus.CANCELADO) {
            throw new BusinessException(
                "Não é possível alterar projeto com status " + project.getStatus().getLabel(), 422);
        }
        if (dto.getDataInicio().isAfter(dto.getPrevisaoTermino())) {
            throw new BusinessException("Data de início deve ser anterior à previsão de término", 422);
        }

        project.setNome(dto.getNome());
        project.setDataInicio(dto.getDataInicio());
        project.setPrevisaoTermino(dto.getPrevisaoTermino());
        project.setDataRealTermino(dto.getDataRealTermino());
        project.setOrcamentoTotal(dto.getOrcamentoTotal());
        project.setDescricao(dto.getDescricao());

        if (dto.getGerenteId() != null) {
            Member gerente = memberRepository.findById(dto.getGerenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Gerente", dto.getGerenteId()));
            project.setGerente(gerente);
        } else {
            project.setGerente(null);
        }

        return projectMapper.toDTO(projectRepository.save(project));
    }

    @Transactional
    public void excluir(Long id) {
        Project project = findById(id);

        if (project.getStatus() == ProjectStatus.INICIADO
            || project.getStatus() == ProjectStatus.EM_ANDAMENTO
            || project.getStatus() == ProjectStatus.ENCERRADO) {
            throw new BusinessException(
                "Não é permitido excluir projeto com status " + project.getStatus().getLabel(), 403);
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponseDTO atualizarStatus(Long id, ProjectStatus novoStatus) {
        Project project = findById(id);

        List<ProjectStatus> proximosValidos = ProjectStatus.getNext(project.getStatus());
        if (!proximosValidos.contains(novoStatus)) {
            throw new BusinessException(
                "Transição de status inválida: " + project.getStatus() + " → " + novoStatus, 422);
        }

        project.setStatus(novoStatus);
        return projectMapper.toDTO(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponseDTO adicionarMembro(Long projetoId, Long membroId) {
        Project project = findById(projetoId);
        Member membro = memberRepository.findById(membroId)
            .orElseThrow(() -> new ResourceNotFoundException("Membro", membroId));

        if (!"funcionario".equalsIgnoreCase(membro.getAtribuicao())) {
            throw new BusinessException("Apenas membros com atribuição 'funcionario' podem ser alocados", 422);
        }

        boolean jaAlocado = project.getMembros().stream().anyMatch(m -> m.getId().equals(membroId));
        if (jaAlocado) {
            throw new BusinessException("Membro já está alocado neste projeto", 422);
        }

        if (project.getMembros().size() >= 10) {
            throw new BusinessException("Projeto já atingiu o limite de 10 membros", 422);
        }

        long projetosAtivos = projectRepository.findByMembros_Id(membroId).stream()
            .filter(p -> p.getStatus() != ProjectStatus.ENCERRADO
                && p.getStatus() != ProjectStatus.CANCELADO)
            .count();
        if (projetosAtivos >= 3) {
            throw new BusinessException("Membro já está alocado em 3 projetos ativos", 422);
        }

        project.getMembros().add(membro);
        return projectMapper.toDTO(projectRepository.save(project));
    }

    @Transactional
    public void removerMembro(Long projetoId, Long membroId) {
        Project project = findById(projetoId);
        memberRepository.findById(membroId)
            .orElseThrow(() -> new ResourceNotFoundException("Membro", membroId));

        boolean estaNoProj = project.getMembros().stream().anyMatch(m -> m.getId().equals(membroId));
        if (!estaNoProj) {
            throw new BusinessException("Membro não está alocado neste projeto", 422);
        }

        if (project.getMembros().size() <= 1) {
            throw new BusinessException("O projeto deve manter ao menos 1 membro alocado", 422);
        }

        project.getMembros().removeIf(m -> m.getId().equals(membroId));
        projectRepository.save(project);
    }

    public PortfolioReportDTO gerarRelatorio() {
        List<Project> projects = projectRepository.findAll();

        Map<String, Long> quantidadePorStatus = projects.stream()
            .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));

        Map<String, BigDecimal> totalOrcadoPorStatus = projects.stream()
            .collect(Collectors.groupingBy(
                p -> p.getStatus().name(),
                Collectors.reducing(BigDecimal.ZERO, Project::getOrcamentoTotal, BigDecimal::add)
            ));

        double mediaEncerrados = projects.stream()
            .filter(p -> p.getStatus() == ProjectStatus.ENCERRADO && p.getDataRealTermino() != null)
            .mapToLong(p -> ChronoUnit.DAYS.between(p.getDataInicio(), p.getDataRealTermino()))
            .average()
            .orElse(0.0);

        long totalMembrosUnicos = projects.stream()
            .flatMap(p -> p.getMembros().stream())
            .map(Member::getId)
            .distinct()
            .count();

        return PortfolioReportDTO.builder()
            .quantidadePorStatus(quantidadePorStatus)
            .totalOrcadoPorStatus(totalOrcadoPorStatus)
            .mediaDuracaoEncerradosEmDias(mediaEncerrados)
            .totalMembrosUnicos(totalMembrosUnicos)
            .build();
    }

    private Project findById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projeto", id));
    }
}
