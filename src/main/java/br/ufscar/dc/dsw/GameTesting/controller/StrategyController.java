package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.model.Example;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import br.ufscar.dc.dsw.GameTesting.service.ExampleService;
import br.ufscar.dc.dsw.GameTesting.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // R5: Cadastro de estratégias (requer login de administrador)
    @PostMapping
    // A segurança para POST /strategies está configurada no SecurityConfig para exigir ROLE_ADMIN
    public ResponseEntity<Strategy> createStrategy(@RequestBody Strategy strategy) {
        // Validação básica do corpo da requisição (pode ser mais complexa com @Valid)
        if (strategy.getName() == null || strategy.getName().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        Strategy savedStrategy = strategyService.save(strategy);
        return new ResponseEntity<>(savedStrategy, HttpStatus.CREATED); // 201 Created
    }

    // R6: Listagem de todas as estratégias (não requer login)
    @GetMapping
    // A segurança para GET /strategies está configurada no SecurityConfig para permitir acesso público
    public ResponseEntity<List<Strategy>> getAllStrategies() {
        List<Strategy> strategies = strategyService.findAll();
        return new ResponseEntity<>(strategies, HttpStatus.OK); // 200 OK
    }

    // Obter uma Strategy por ID
    @GetMapping("/{id}")
    public ResponseEntity<Strategy> getStrategyById(@PathVariable Long id) {
        Optional<Strategy> strategy = strategyService.findById(id);
        return strategy.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    // Atualizar uma Strategy existente (requer login de administrador)
    @PutMapping("/{id}")
    // A segurança para PUT /strategies/{id} está configurada no SecurityConfig para exigir ROLE_ADMIN
    public ResponseEntity<Strategy> updateStrategy(@PathVariable Long id, @RequestBody Strategy strategyDetails) {
        // Garante que o ID da URL corresponde ao ID no corpo, se fornecido
        if (strategyDetails.getId() != null && !strategyDetails.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        strategyDetails.setId(id); // Garante que o ID do objeto seja o ID do path

        // Tenta buscar a estratégia existente para garantir que ela exista antes de salvar
        return strategyService.findById(id).map(existingStrategy -> {
            // Atualiza apenas os campos permitidos
            existingStrategy.setName(strategyDetails.getName());
            existingStrategy.setDescription(strategyDetails.getDescription());

            existingStrategy.getExamples().clear();
            if (strategyDetails.getExamples() != null) {
                for (Example example : strategyDetails.getExamples()) {
                    example.setStrategy(existingStrategy); 
                    if (example.getImage() != null) {
                        example.getImage().setExample(example); 
                    }
                    existingStrategy.getExamples().add(example);
                }
            }
            existingStrategy.setTips(strategyDetails.getTips()); 


            Strategy updatedStrategy = strategyService.save(existingStrategy); // Reusa o método save para persistência
            return new ResponseEntity<>(updatedStrategy, HttpStatus.OK); // 200 OK
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }


    // Deletar uma Strategy (requer login de administrador)
    @DeleteMapping("/{id}")
    // A segurança para DELETE /strategies/{id} está configurada no SecurityConfig para exigir ROLE_ADMIN
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        try {
            strategyService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            // Captura a exceção lançada pelo serviço se a Strategy não for encontrada
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    // Adicionar um Example a uma Strategy existente (requer login de administrador)
    @PostMapping("/{strategyId}/examples")
    public ResponseEntity<Example> addExampleToStrategy(
            @PathVariable Long strategyId,
            @RequestBody Example example) {
        try {
            // Este método chama ExampleService.save(example, strategyId) que é o correto
            Example savedExample = exampleService.save(example, strategyId);
            return new ResponseEntity<>(savedExample, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Se a Strategy não for encontrada
        }
    }

    // Listar Examples de uma Strategy específica
    @GetMapping("/{strategyId}/examples")
    public ResponseEntity<List<Example>> getExamplesByStrategy(@PathVariable Long strategyId) {
        // Este método chama ExampleService.findByStrategyId(strategyId) que é o correto
        List<Example> examples = exampleService.findByStrategyId(strategyId);
        if (examples.isEmpty()) {
            // Retorna 404 se a strategy não existir ou se não tiver exemplos
            if (!strategyService.findById(strategyId).isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(examples, HttpStatus.OK); // 200 OK, lista vazia
        }
        return new ResponseEntity<>(examples, HttpStatus.OK);
    }

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