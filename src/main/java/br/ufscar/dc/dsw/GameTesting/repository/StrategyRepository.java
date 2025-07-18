package br.ufscar.dc.dsw.GameTesting.repository; // Ou .repository

import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Import para Optional

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {

    // Encontrar estratégia pelo nome
    Optional<Strategy> findByName(String name);

    @EntityGraph(attributePaths = {"examples", "examples.image"})
    List<Strategy> findAll();

    // Encontrar estratégia por session
   // Optional<Strategy> findBySessionId(Long sessionId);
}