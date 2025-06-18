package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.CreateStrategyDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ExampleDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.StrategyResponseDTO;
import br.ufscar.dc.dsw.GameTesting.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import br.ufscar.dc.dsw.GameTesting.service.ExampleService;
import br.ufscar.dc.dsw.GameTesting.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategies")
public class StrategyController {

    private final StrategyService strategyService;
    private final ExampleService exampleService;
    private final ImageService imageService;

    @Autowired
    public StrategyController(StrategyService strategyService,
                              ExampleService exampleService,
                              ImageService imageService) {
        this.strategyService = strategyService;
        this.exampleService = exampleService;
        this.imageService = imageService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")  // TESTED - OK
    public ResponseEntity<?> createStrategy(@RequestBody CreateStrategyDTO strategyDTO) {
        Strategy savedStrategy = strategyService.save(strategyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStrategy);
    }

    @GetMapping("")    // TESTED - OK
    public ResponseEntity<?> getAllStrategies() {
        List<StrategyResponseDTO> responseDTO = strategyService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/{id}")   // TESTED - OK
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> getStrategyById(@PathVariable Long id) {
        StrategyResponseDTO responseDTO = strategyService.findById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{id}") // TESTED - OK
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> updateStrategy(@PathVariable Long id, @RequestBody CreateStrategyDTO dto) {
        try {
            StrategyResponseDTO updated = strategyService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")  // TESTED - OK
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        try {
            strategyService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/examples/{strategyId}") // TESTED - OK
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> addExampleToStrategy(@PathVariable Long strategyId, @RequestBody ExampleDTO exampleDTO) {
        try {
            ExampleDTO responseDTO = exampleService.save(exampleDTO, strategyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/examples/{strategyId}")
    @PreAuthorize("hasRole('ADMIN')")  // TESTED - OK
    public ResponseEntity<?> getExamplesByStrategyId(@PathVariable Long strategyId) {
        try {
            List<ExampleDTO> responseDTO = exampleService.findByStrategyId(strategyId);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/examples/{id}")
    @PreAuthorize("hasRole('ADMIN')") // TESTED - OK
    public ResponseEntity<Void> deleteExample(@PathVariable Long id) {
        try {
            exampleService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/images")
    @PreAuthorize("hasRole('ADMIN')") // TESTED - OK
    public ResponseEntity<?> uploadImage(@RequestBody ImageDTO imageDTO) {
        try {
            ImageDTO responseDTO = imageService.saveImage(imageDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/images")
    @PreAuthorize("hasRole('ADMIN')")  // TESTED - OK
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageService.findAllImages();
        return ResponseEntity.status(HttpStatus.OK).body(images);
    }

    @DeleteMapping("/images/{id}")   // TESTED - OK
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}