package com.portfolio.manager.domain.enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.portfolio.manager.domain.enums.ProjectStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class ProjectStatusTest {

    @Test
    void emAnalise_nextContemAnaliseRealizada() {
        assertThat(getNext(EM_ANALISE)).contains(ANALISE_REALIZADA);
    }

    @Test
    void emAnalise_nextNaoContemIniciado() {
        assertThat(getNext(EM_ANALISE)).doesNotContain(INICIADO);
    }

    @Test
    void emAnalise_nextNaoContemPlanejado() {
        assertThat(getNext(EM_ANALISE)).doesNotContain(PLANEJADO);
    }

    @Test
    void todosOsStatusPermitemTransicaoParaCancelado_excetoCancelado() {
        List<ProjectStatus> statusFinais = List.of(ENCERRADO, CANCELADO);
        for (ProjectStatus status : values()) {
            if (!statusFinais.contains(status)) {
                assertThat(getNext(status))
                    .as("Status %s deve permitir transição para CANCELADO", status)
                    .contains(CANCELADO);
            }
        }
    }

    @Test
    void encerrado_semProximoStatus() {
        assertThat(getNext(ENCERRADO)).isEmpty();
    }

    @Test
    void cancelado_semProximoStatus() {
        assertThat(getNext(CANCELADO)).isEmpty();
    }

    @Test
    void sequenciaCompleta_cadaStatusTransicionaParaProximo() {
        assertThat(getNext(EM_ANALISE)).contains(ANALISE_REALIZADA);
        assertThat(getNext(ANALISE_REALIZADA)).contains(ANALISE_APROVADA);
        assertThat(getNext(ANALISE_APROVADA)).contains(INICIADO);
        assertThat(getNext(INICIADO)).contains(PLANEJADO);
        assertThat(getNext(PLANEJADO)).contains(EM_ANDAMENTO);
        assertThat(getNext(EM_ANDAMENTO)).contains(ENCERRADO);
    }

    @Test
    void naoPermitePularEtapas_analiseRealizadaNaoContemIniciado() {
        assertThat(getNext(ANALISE_REALIZADA)).doesNotContain(INICIADO);
        assertThat(getNext(ANALISE_REALIZADA)).doesNotContain(PLANEJADO);
    }

    @Test
    void naoPermiteVoltarStatus() {
        assertThat(getNext(ANALISE_REALIZADA)).doesNotContain(EM_ANALISE);
        assertThat(getNext(INICIADO)).doesNotContain(ANALISE_APROVADA);
        assertThat(getNext(EM_ANDAMENTO)).doesNotContain(PLANEJADO);
    }

    @Test
    void emAndamento_proximosContemEncerradoECancelado() {
        List<ProjectStatus> next = getNext(EM_ANDAMENTO);
        assertThat(next).containsExactlyInAnyOrder(ENCERRADO, CANCELADO);
    }
}
