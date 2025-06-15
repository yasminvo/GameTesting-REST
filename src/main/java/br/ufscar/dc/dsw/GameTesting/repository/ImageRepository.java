package br.ufscar.dc.dsw.GameTesting.repository; 

import br.ufscar.dc.dsw.GameTesting.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // JpaRepository fornece métodos CRUD básicos (save, findById, findAll, delete, etc.)
}   