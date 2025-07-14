package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.ProjetoDTO;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjetoService projetoService;

    public ProjectController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @GetMapping("")
    public ResponseEntity<List<ProjetoDTO>> getAllProjetos(@RequestParam(defaultValue = "creationDate") String sort, Locale locale) {
        List<ProjetoDTO> response = projetoService.listAllSorted(sort);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoDTO> getProjetoById(@PathVariable Long id, Locale locale) {
        ProjetoDTO response = projetoService.getById(id, locale);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<ProjetoDTO> createProjeto(@RequestBody ProjetoDTO projetoDTO, Locale locale) {
        ProjetoDTO response = projetoService.createProjeto(projetoDTO, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProjetoDTO> updateProjeto(@PathVariable Long id, @RequestBody ProjetoDTO projetoDTO, Locale locale) {
        ProjetoDTO response = projetoService.updateProjeto(id, projetoDTO, locale);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjeto(@PathVariable Long id, Locale locale) {
        boolean deleted = projetoService.deleteProjeto(id, locale);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
