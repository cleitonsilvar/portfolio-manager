package com.portfolio.manager.domain.repository;

import com.portfolio.manager.domain.entity.Project;
import com.portfolio.manager.domain.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByMembros_Id(Long membroId);
}
