package com.portfolio.manager.domain.repository;

import com.portfolio.manager.domain.entity.Project;
import com.portfolio.manager.domain.enums.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {

    private ProjectSpecification() {}

    public static Specification<Project> nomeContains(String nome) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Project> statusEquals(ProjectStatus status) {
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

    public static Specification<Project> gerenteIdEquals(Long gerenteId) {
        return (root, query, cb) ->
            cb.equal(root.get("gerente").get("id"), gerenteId);
    }
}
