package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.*;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import br.ufscar.dc.dsw.GameTesting.service.SessionService;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final ProjetoService projetoService;
    private final StrategyService strategyService;

    @Autowired
    public SessionController(SessionService sessionService, ProjetoService projetoService,
            StrategyService strategyService) {
        this.sessionService = sessionService;
        this.projetoService = projetoService;
        this.strategyService = strategyService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> listAll(Authentication auth) {
        List<SessionResponseDTO> sessions = sessionService.listByRole(auth);
        return ResponseEntity.ok(sessions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> getById(@PathVariable Long id) {
        SessionResponseDTO session = sessionService.findSessionById(id);
        return ResponseEntity.ok(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody SessionCreateDTO createDTO) {
        sessionService.createSession(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody SessionUpdateDTO updateDTO,
            Principal principal) {
        sessionService.updateSession(id, updateDTO, principal);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable Long id) {
        try {
            sessionService.startSession(id);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            throw new AppException("Acesso negado ao iniciar sessão.", HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            throw new AppException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            throw new AppException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/finalize")
    public ResponseEntity<Void> finalize(@PathVariable Long id) {
        try {
            sessionService.finalizeSession(id);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            throw new AppException("Acesso negado ao finalizar sessão.", HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            throw new AppException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            throw new AppException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{sessionId}/bugs")
    public ResponseEntity<Void> reportBug(@PathVariable Long sessionId, @RequestBody BugDTO bugDto) {
        sessionService.reportBug(sessionId, bugDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{sessionId}/bugs")
    public ResponseEntity<List<BugDTO>> listBugs(@PathVariable Long sessionId) {
        List<BugDTO> bugs = sessionService.findBugsBySession(sessionId);
        return ResponseEntity.ok(bugs);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/resources")
    public ResponseEntity<?> getFormResources() {
        List<ProjetoDTO> projetos = projetoService.listAllSorted("name");
        List<StrategyResponseDTO> estrategias = strategyService.findAll();

        return ResponseEntity.ok(new FormResourcesDTO(projetos, estrategias));
    }

    public record FormResourcesDTO(
            List<ProjetoDTO> projetos,
            List<StrategyResponseDTO> estrategias) {
    }
}
