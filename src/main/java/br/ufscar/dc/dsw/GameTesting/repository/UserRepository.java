package br.ufscar.dc.dsw.GameTesting.repository;

import br.ufscar.dc.dsw.GameTesting.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
