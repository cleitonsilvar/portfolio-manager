package com.portfolio.manager.service;

import com.portfolio.manager.domain.entity.Member;
import com.portfolio.manager.domain.entity.Project;
import com.portfolio.manager.domain.enums.ProjectStatus;
import com.portfolio.manager.domain.repository.MemberRepository;
import com.portfolio.manager.domain.repository.ProjectRepository;
import com.portfolio.manager.dto.request.ProjectRequestDTO;
import com.portfolio.manager.dto.response.ProjectResponseDTO;
import com.portfolio.manager.exception.BusinessException;
import com.portfolio.manager.mapper.ProjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.portfolio.manager.dto.request.ProjectFilterDTO;
import com.portfolio.manager.dto.response.PortfolioReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProjectMapper projectMapper;
    @InjectMocks private ProjectService projectService;

    // ── excluir ───────────────────────────────────────────────────────────────

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"INICIADO", "EM_ANDAMENTO", "ENCERRADO"})
    void excluir_statusProibido_lancaBusinessException(ProjectStatus status) {
        Project project = projectComStatus(status);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.excluir(1L))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(403);
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"EM_ANALISE", "CANCELADO"})
    void excluir_statusPermitido_deletaComSucesso(ProjectStatus status) {
        Project project = projectComStatus(status);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.excluir(1L);

        verify(projectRepository).delete(project);
    }

    // ── atualizarStatus ───────────────────────────────────────────────────────

    @Test
    void atualizarStatus_transicaoValida_salvaNovoStatus() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        ProjectResponseDTO dto = new ProjectResponseDTO();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDTO(any())).thenReturn(dto);

        projectService.atualizarStatus(1L, ProjectStatus.ANALISE_REALIZADA);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
        verify(projectRepository).save(project);
    }

    @Test
    void atualizarStatus_pularEtapa_lancaBusinessException422() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.atualizarStatus(1L, ProjectStatus.INICIADO))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void atualizarStatus_qualquerStatusParaCancelado_permitido() {
        for (ProjectStatus status : new ProjectStatus[]{
            ProjectStatus.EM_ANALISE, ProjectStatus.ANALISE_REALIZADA,
            ProjectStatus.ANALISE_APROVADA, ProjectStatus.INICIADO,
            ProjectStatus.PLANEJADO, ProjectStatus.EM_ANDAMENTO
        }) {
            Project project = projectComStatus(status);
            when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            when(projectRepository.save(any())).thenReturn(project);
            when(projectMapper.toDTO(any())).thenReturn(new ProjectResponseDTO());

            projectService.atualizarStatus(1L, ProjectStatus.CANCELADO);

            assertThat(project.getStatus()).isEqualTo(ProjectStatus.CANCELADO);
        }
    }

    // ── adicionarMembro ───────────────────────────────────────────────────────

    @Test
    void adicionarMembro_membroNaoFuncionario_lancaBusinessException422() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        Member gerente = Member.builder().id(2L).nome("Gerente").atribuicao("gerente").build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(gerente));

        assertThatThrownBy(() -> projectService.adicionarMembro(1L, 2L))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void adicionarMembro_membroJaEm3ProjetosAtivos_lancaBusinessException422() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        Member membro = Member.builder().id(2L).nome("João").atribuicao("funcionario").build();

        List<Project> projetosAtivos = List.of(
            projectComStatus(ProjectStatus.EM_ANALISE),
            projectComStatus(ProjectStatus.INICIADO),
            projectComStatus(ProjectStatus.EM_ANDAMENTO)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(membro));
        when(projectRepository.findByMembros_Id(2L)).thenReturn(projetosAtivos);

        assertThatThrownBy(() -> projectService.adicionarMembro(1L, 2L))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void adicionarMembro_projetoComDezMembros_lancaBusinessException422() {
        List<Member> membros = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            membros.add(Member.builder().id((long) i).nome("M" + i).atribuicao("funcionario").build());
        }
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(membros).build();

        Member novoMembro = Member.builder().id(11L).nome("Novo").atribuicao("funcionario").build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(11L)).thenReturn(Optional.of(novoMembro));

        assertThatThrownBy(() -> projectService.adicionarMembro(1L, 11L))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void adicionarMembro_membroJaNoProjet_lancaBusinessException422() {
        Member membro = Member.builder().id(2L).nome("Ana").atribuicao("funcionario").build();
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(new ArrayList<>(List.of(membro))).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(membro));

        assertThatThrownBy(() -> projectService.adicionarMembro(1L, 2L))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void adicionarMembro_casoValido_adicionaComSucesso() {
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(new ArrayList<>()).build();
        Member membro = Member.builder().id(2L).nome("Ana").atribuicao("funcionario").build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(membro));
        when(projectRepository.findByMembros_Id(2L)).thenReturn(List.of());
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDTO(any())).thenReturn(new ProjectResponseDTO());

        projectService.adicionarMembro(1L, 2L);

        assertThat(project.getMembros()).contains(membro);
        verify(projectRepository).save(project);
    }

    // ── removerMembro ─────────────────────────────────────────────────────────

    @Test
    void removerMembro_membroNaoEstaNoProjet_lancaBusinessException() {
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(new ArrayList<>()).build();
        Member membro = Member.builder().id(2L).nome("Ana").atribuicao("funcionario").build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(membro));

        assertThatThrownBy(() -> projectService.removerMembro(1L, 2L))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void removerMembro_ficariaSemMembros_lancaBusinessException() {
        Member membro = Member.builder().id(2L).nome("Ana").atribuicao("funcionario").build();
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(new ArrayList<>(List.of(membro))).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(membro));

        assertThatThrownBy(() -> projectService.removerMembro(1L, 2L))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    void removerMembro_casoValido_removeComSucesso() {
        Member m1 = Member.builder().id(1L).nome("Ana").atribuicao("funcionario").build();
        Member m2 = Member.builder().id(2L).nome("Bob").atribuicao("funcionario").build();
        Project project = Project.builder()
            .id(1L).nome("P").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.now()).previsaoTermino(LocalDate.now().plusMonths(3))
            .orcamentoTotal(BigDecimal.TEN).membros(new ArrayList<>(List.of(m1, m2))).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(m2));
        when(projectRepository.save(any())).thenReturn(project);

        projectService.removerMembro(1L, 2L);

        assertThat(project.getMembros()).doesNotContain(m2);
        verify(projectRepository).save(project);
    }

    // ── criar ────────────────────────────────────────────────────────────────

    @Test
    void criar_dataInicioAposPrevisaoTermino_lancaBusinessException422() {
        ProjectRequestDTO dto = new ProjectRequestDTO(
            "Proj", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 1, 1),
            null, BigDecimal.valueOf(50000), null, null
        );

        assertThatThrownBy(() -> projectService.criar(dto))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void criar_casoValido_salvaERetornaDTO() {
        ProjectRequestDTO dto = new ProjectRequestDTO(
            "Proj", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1),
            null, BigDecimal.valueOf(50000), null, null
        );
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();

        when(projectMapper.toEntity(dto)).thenReturn(project);
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDTO(project)).thenReturn(responseDTO);

        ProjectResponseDTO result = projectService.criar(dto);

        assertThat(result).isNotNull();
        verify(projectRepository).save(project);
    }

    // ── buscarPorId ───────────────────────────────────────────────────────────

    @Test
    void buscarPorId_projetoExistente_retornaDTO() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        ProjectResponseDTO dto = new ProjectResponseDTO();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDTO(project)).thenReturn(dto);

        ProjectResponseDTO result = projectService.buscarPorId(1L);

        assertThat(result).isNotNull();
    }

    // ── listar ────────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void listar_semFiltros_retornaPaginaVazia() {
        Page<Project> emptyPage = new PageImpl<>(List.of());
        when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(emptyPage);

        Page<ProjectResponseDTO> result = projectService.listar(
            new ProjectFilterDTO(), PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_comFiltroNome_retornaPaginaFiltrada() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        ProjectResponseDTO dto = new ProjectResponseDTO();
        Page<Project> page = new PageImpl<>(List.of(project));
        when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(page);
        when(projectMapper.toDTO(project)).thenReturn(dto);

        ProjectFilterDTO filter = new ProjectFilterDTO("Alpha", null, null);
        Page<ProjectResponseDTO> result = projectService.listar(filter, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_comFiltroStatusEGerenteId_passaSpecification() {
        Page<Project> emptyPage = new PageImpl<>(List.of());
        when(projectRepository.findAll(any(Specification.class), any(PageRequest.class)))
            .thenReturn(emptyPage);

        ProjectFilterDTO filter = new ProjectFilterDTO(null, ProjectStatus.INICIADO, 1L);
        projectService.listar(filter, PageRequest.of(0, 10));

        verify(projectRepository).findAll(any(Specification.class), any(PageRequest.class));
    }

    // ── atualizar ─────────────────────────────────────────────────────���──────

    @Test
    void atualizar_casoValido_atualizaERetornaDTO() {
        Project project = projectComStatus(ProjectStatus.EM_ANALISE);
        ProjectRequestDTO dto = new ProjectRequestDTO(
            "Novo Nome", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1),
            null, BigDecimal.valueOf(80000), "Desc", null
        );
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDTO(any())).thenReturn(responseDTO);

        ProjectResponseDTO result = projectService.atualizar(1L, dto);

        assertThat(result).isNotNull();
        assertThat(project.getNome()).isEqualTo("Novo Nome");
    }

    @Test
    void atualizar_statusEncerrado_lancaBusinessException() {
        Project project = projectComStatus(ProjectStatus.ENCERRADO);
        ProjectRequestDTO dto = new ProjectRequestDTO(
            "Nome", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1),
            null, BigDecimal.valueOf(50000), null, null
        );
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.atualizar(1L, dto))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    @Test
    void atualizar_statusCancelado_lancaBusinessException() {
        Project project = projectComStatus(ProjectStatus.CANCELADO);
        ProjectRequestDTO dto = new ProjectRequestDTO(
            "Nome", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1),
            null, BigDecimal.valueOf(50000), null, null
        );
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.atualizar(1L, dto))
            .isInstanceOf(BusinessException.class)
            .extracting(e -> ((BusinessException) e).getStatusCode())
            .isEqualTo(422);
    }

    // ── gerarRelatorio ───────────────────────────────────────────────────────

    @Test
    void gerarRelatorio_semProjetos_retornaRelatorioVazio() {
        when(projectRepository.findAll()).thenReturn(List.of());

        PortfolioReportDTO result = projectService.gerarRelatorio();

        assertThat(result.getQuantidadePorStatus()).isEmpty();
        assertThat(result.getTotalOrcadoPorStatus()).isEmpty();
        assertThat(result.getMediaDuracaoEncerradosEmDias()).isEqualTo(0.0);
        assertThat(result.getTotalMembrosUnicos()).isEqualTo(0L);
    }

    @Test
    void gerarRelatorio_comProjetosEMembros_calculaCorretamente() {
        Member m1 = Member.builder().id(1L).nome("Ana").atribuicao("funcionario").build();
        Member m2 = Member.builder().id(2L).nome("Bob").atribuicao("funcionario").build();

        Project encerrado = Project.builder()
            .id(1L).nome("P1").status(ProjectStatus.ENCERRADO)
            .dataInicio(LocalDate.of(2025, 1, 1))
            .previsaoTermino(LocalDate.of(2025, 6, 1))
            .dataRealTermino(LocalDate.of(2025, 5, 1))
            .orcamentoTotal(BigDecimal.valueOf(100000))
            .membros(new ArrayList<>(List.of(m1, m2))).build();

        Project ativo = Project.builder()
            .id(2L).nome("P2").status(ProjectStatus.EM_ANALISE)
            .dataInicio(LocalDate.of(2025, 1, 1))
            .previsaoTermino(LocalDate.of(2025, 6, 1))
            .orcamentoTotal(BigDecimal.valueOf(50000))
            .membros(new ArrayList<>(List.of(m1))).build();

        when(projectRepository.findAll()).thenReturn(List.of(encerrado, ativo));

        PortfolioReportDTO result = projectService.gerarRelatorio();

        assertThat(result.getQuantidadePorStatus()).containsKey("ENCERRADO");
        assertThat(result.getQuantidadePorStatus().get("ENCERRADO")).isEqualTo(1L);
        assertThat(result.getTotalMembrosUnicos()).isEqualTo(2L);
        assertThat(result.getMediaDuracaoEncerradosEmDias()).isGreaterThan(0.0);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Project projectComStatus(ProjectStatus status) {
        return Project.builder()
            .id(1L)
            .nome("Projeto Teste")
            .status(status)
            .dataInicio(LocalDate.of(2025, 1, 1))
            .previsaoTermino(LocalDate.of(2025, 6, 1))
            .orcamentoTotal(BigDecimal.valueOf(50000))
            .membros(new ArrayList<>())
            .build();
    }
}
