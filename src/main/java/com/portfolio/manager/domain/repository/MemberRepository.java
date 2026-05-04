package com.portfolio.manager.domain.repository;

import com.portfolio.manager.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByAtribuicaoIgnoreCase(String atribuicao);
}
