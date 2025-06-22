package br.ufscar.dc.dsw.GameTesting.repository;

import br.ufscar.dc.dsw.GameTesting.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findBySessionId(Long sessionId);
}