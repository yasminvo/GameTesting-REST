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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("strategy", new CreateStrategyDTO());
        return "strategies/create"; // templates/strategies/create.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/create")
    public String createStrategy(@ModelAttribute("strategy") CreateStrategyDTO strategyDTO) {
        strategyService.save(strategyDTO);
        return "redirect:/strategies/list";
    }

    @GetMapping("/list")
    public String listStrategies(Model model) {
        List<StrategyResponseDTO> strategies = strategyService.findAll();
        model.addAttribute("strategies", strategies);
        return "strategies/list"; // templates/strategies/list.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StrategyResponseDTO strategy = strategyService.findById(id);
        model.addAttribute("strategy", strategy);
        return "strategies/edit"; // templates/strategies/edit.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/edit/{id}")
    public String updateStrategy(@PathVariable Long id,
                                 @ModelAttribute("strategy") CreateStrategyDTO dto) {
        strategyService.update(id, dto);
        return "redirect:/strategies/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/delete/{id}")
    public String deleteStrategy(@PathVariable Long id) {
        strategyService.delete(id);
        return "redirect:/strategies/list";
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