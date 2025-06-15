package br.ufscar.dc.dsw.GameTesting.repository;

import br.ufscar.dc.dsw.GameTesting.enums.Status;
import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByTester(User tester);
    List<Session> findByProjeto(Projeto projeto);
    List<Session> findByStatus(Status status);
    List<Session> findByProjetoId(Long projetoId);

    List<Session> findByProjetoIdAndTester(Long projetoId, User tester);
}