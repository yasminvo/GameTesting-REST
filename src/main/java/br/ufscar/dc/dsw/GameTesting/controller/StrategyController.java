package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.CreateStrategyDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ExampleDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.ImageDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.StrategyResponseDTO;
import br.ufscar.dc.dsw.GameTesting.model.Image;
import br.ufscar.dc.dsw.GameTesting.model.Strategy;
import br.ufscar.dc.dsw.GameTesting.service.ExampleService;
import br.ufscar.dc.dsw.GameTesting.service.ImageService;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/strategies")
public class StrategyController {

    private final StrategyService strategyService;
    private final ExampleService exampleService;
    private final ImageService imageService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public StrategyController(StrategyService strategyService,
                              ExampleService exampleService,
                              ImageService imageService) {
        this.strategyService = strategyService;
        this.exampleService = exampleService;
        this.imageService = imageService;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> createStrategy(@RequestBody CreateStrategyDTO strategyDTO, Locale locale) {
        Strategy savedStrategy = strategyService.save(strategyDTO, locale);
        String msg = messageSource.getMessage("strategy.created.success", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllStrategies(Locale locale) {
        List<StrategyResponseDTO> responseDTO = strategyService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> getStrategyById(@PathVariable Long id, Locale locale) {
        try {
            StrategyResponseDTO responseDTO = strategyService.findById(id, locale);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (EntityNotFoundException e) {
            String msg = messageSource.getMessage("strategy.notfound", new Object[]{id}, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> updateStrategy(@PathVariable Long id, @RequestBody CreateStrategyDTO dto, Locale locale) {
        try {
            StrategyResponseDTO updated = strategyService.update(id, dto, locale);
            String msg = messageSource.getMessage("strategy.updated.success", null, locale);
            return ResponseEntity.ok(msg);
        } catch (EntityNotFoundException e) {
            String msg = messageSource.getMessage("strategy.notfound", new Object[]{id}, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> deleteStrategy(@PathVariable Long id, Locale locale) {
        try {
            strategyService.delete(id, locale);
            String msg = messageSource.getMessage("strategy.deleted.success", null, locale);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        } catch (RuntimeException e) {
            String msg = messageSource.getMessage("strategy.notfound", new Object[]{id}, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
    }

    @PostMapping("/examples/{strategyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    public ResponseEntity<?> addExampleToStrategy(@PathVariable Long strategyId, @RequestBody ExampleDTO exampleDTO, Locale locale) {
        try {
            ExampleDTO responseDTO = exampleService.save(exampleDTO, strategyId, locale);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (EntityNotFoundException e) {
            String msg = messageSource.getMessage("strategy.notfound", new Object[]{strategyId}, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
    }

    @GetMapping("/examples/{strategyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getExamplesByStrategyId(@PathVariable Long strategyId, Locale locale) {
        try {
            List<ExampleDTO> responseDTO = exampleService.findByStrategyId(strategyId, locale);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (EntityNotFoundException e) {
            String msg = messageSource.getMessage("strategy.notfound", new Object[]{strategyId}, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
        }
    }

    @DeleteMapping("/examples/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExample(@PathVariable Long id, Locale locale) {
        try {
            exampleService.delete(id, locale);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(@RequestBody ImageDTO imageDTO, Locale locale) {
        try {
            ImageDTO responseDTO = imageService.saveImage(imageDTO, locale);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            String msg = messageSource.getMessage("image.upload.badrequest", null, locale);
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/images")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Image>> getAllImages(Locale locale) {
        List<Image> images = imageService.findAllImages(locale);
        return ResponseEntity.status(HttpStatus.OK).body(images);
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id, Locale locale) {
        try {
            imageService.deleteImage(id, locale);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            String msg = messageSource.getMessage("image.notfound", new Object[]{id}, locale);
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
    }
}
