package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.model.Projeto;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @GetMapping("/")
    public List<Projeto> getAllProjetos() {
        return projetoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> getProjetoById(@PathVariable Long id) {
        Optional<Projeto> projeto = projetoService.findById(id);
        return projeto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<Projeto> createProjeto(@RequestBody Projeto projeto) {
        Projeto createdProjeto = projetoService.save(projeto);
        return ResponseEntity.ok(createdProjeto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> updateProjeto(@PathVariable Long id, @RequestBody Projeto projetoDetails) {
        Optional<Projeto> existingProjeto = projetoService.findById(id);
        if (existingProjeto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Projeto projeto = existingProjeto.get();
        projeto.setName(projetoDetails.getName());
        projeto.setDescription(projetoDetails.getDescription());
        projeto.setCreationDate(projetoDetails.getCreationDate());
        projeto.setMembers(projetoDetails.getMembers()); // cuidado com sobrescrita!

        Projeto updatedProjeto = projetoService.save(projeto);
        return ResponseEntity.ok(updatedProjeto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjeto(@PathVariable Long id) {
        Optional<Projeto> existingProjeto = projetoService.findById(id);
        if (existingProjeto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        projetoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
