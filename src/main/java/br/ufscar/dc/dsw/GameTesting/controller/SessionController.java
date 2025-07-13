package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.*;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.Session;
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

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> listAll(Authentication auth) {
        List<SessionResponseDTO> sessions = sessionService.listByRole(auth);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> getById(@PathVariable Long id) {
        SessionResponseDTO session = sessionService.findSessionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<SessionResponseDTO>> getByProjectId(@PathVariable Long projectId) {
        List<SessionResponseDTO> sessions = sessionService.findSessionsByProjectId(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping
    public ResponseEntity<Session> create(@RequestBody SessionCreateDTO createDTO) {
        Session session = sessionService.createSession(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> update(@PathVariable Long id, @RequestBody SessionUpdateDTO updateDTO,
            Principal principal) {
        SessionResponseDTO response = sessionService.updateSession(id, updateDTO, principal);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/start")
    public ResponseEntity<Session> start(@PathVariable Long id) {
        Session session = sessionService.startSession(id);
        return ResponseEntity.status(HttpStatus.OK).body(session);

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/finalize")
    public ResponseEntity<Session> finalize(@PathVariable Long id) {
        Session session = sessionService.finalizeSession(id);
        return ResponseEntity.status(HttpStatus.OK).body(session);

    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{sessionId}/bugs")
    public ResponseEntity<BugDTO> reportBug(@PathVariable Long sessionId, @RequestBody BugDTO bugDto) {
        BugDTO response = sessionService.reportBug(sessionId, bugDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{sessionId}/bugs")
    public ResponseEntity<List<BugDTO>> listBugs(@PathVariable Long sessionId) {
        List<BugDTO> bugs = sessionService.findBugsBySession(sessionId);
        return ResponseEntity.ok(bugs);
    }

}
