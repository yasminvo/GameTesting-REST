package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.CreateStrategyDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.StrategyResponseDTO;
import br.ufscar.dc.dsw.GameTesting.exceptions.ResourceNotFoundException;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
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
import java.util.Optional;

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
    @PreAuthorize("hasRole('ADMIN')")  // TESTED - OK
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStrategyById(@PathVariable Long id) {
        StrategyResponseDTO responseDTO = strategyService.findById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{id}") // TESTED - OK
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStrategy(@PathVariable Long id, @RequestBody CreateStrategyDTO dto) {
        try {
            StrategyResponseDTO updated = strategyService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")  // TESTED - OK
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        try {
            strategyService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @PostMapping("/{strategyId}/examples")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Example> addExampleToStrategy(
//            @PathVariable Long strategyId,
//            @RequestBody Example example) {
//        try {
//            // Este método chama ExampleService.save(example, strategyId) que é o correto
//            Example savedExample = exampleService.save(example, strategyId);
//            return new ResponseEntity<>(savedExample, HttpStatus.CREATED);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Se a Strategy não for encontrada
//        }
//    }

//    // Listar Examples de uma Strategy específica
//    @GetMapping("/{strategyId}/examples")
//    public ResponseEntity<List<Example>> getExamplesByStrategy(@PathVariable Long strategyId) {
//        // Este método chama ExampleService.findByStrategyId(strategyId) que é o correto
//        List<Example> examples = exampleService.findByStrategyId(strategyId);
//        if (examples.isEmpty()) {
//            // Retorna 404 se a strategy não existir ou se não tiver exemplos
//            if (!strategyService.findById(strategyId).isPresent()) {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(examples, HttpStatus.OK); // 200 OK, lista vazia
//        }
//        return new ResponseEntity<>(examples, HttpStatus.OK);
//    }

    // Obter um Example específico
    @GetMapping("/examples/{id}")
    public ResponseEntity<Example> getExampleById(@PathVariable Long id) {
        // Este método chama ExampleService.findById(id) que é o correto
        Optional<Example> example = exampleService.findById(id);
        return example.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Deletar um Example (requer login de administrador)
    @DeleteMapping("/examples/{id}")
    public ResponseEntity<Void> deleteExample(@PathVariable Long id) {
        try {
            // Este método chama ExampleService.delete(id) que é o correto
            exampleService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Upload/Salvar uma Image (requer login de administrador)
    @PostMapping("/images")
    public ResponseEntity<Image> uploadImage(@RequestBody Image image) {
        try {
            // Este método chama ImageService.saveImage(image) que é o correto
            Image savedImage = imageService.saveImage(image);
            return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Se filePath estiver vazio
        }
    }

    // Obter uma Image por ID
    @GetMapping("/images/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        // Este método chama ImageService.findImageById(id) que é o correto
        Optional<Image> image = imageService.findImageById(id);
        return image.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Deletar uma Image
    @DeleteMapping("/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        try {
            // Este método chama ImageService.deleteImage(id) que é o correto
            imageService.deleteImage(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}