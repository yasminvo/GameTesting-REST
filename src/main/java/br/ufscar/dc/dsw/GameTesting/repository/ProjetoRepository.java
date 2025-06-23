package br.ufscar.dc.dsw.GameTesting.repository;

import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

     List<Projeto> findByNameContainingIgnoreCase(String name);
     List<Projeto> findByMembersId(Long userId, Sort sort);


}
