package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.*;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.service.SessionService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> listAll(Authentication auth, Locale locale) {
        List<SessionResponseDTO> sessions = sessionService.listByRole(auth);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> getById(@PathVariable Long id, Locale locale) {
        SessionResponseDTO session = sessionService.findSessionById(id, locale);
        return ResponseEntity.status(HttpStatus.OK).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/byProject/{projectId}")
    public ResponseEntity<List<SessionResponseDTO>> getByProjectId(@PathVariable Long projectId, Locale locale) {
        List<SessionResponseDTO> sessions = sessionService.findSessionsByProjectId(projectId, locale);
        return ResponseEntity.status(HttpStatus.OK).body(sessions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping
    public ResponseEntity<Session> create(@RequestBody SessionCreateDTO createDTO, Locale locale) {
        Session session = sessionService.createSession(createDTO, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> update(@PathVariable Long id, @RequestBody SessionUpdateDTO updateDTO,
                                                     Principal principal, Locale locale) {
        SessionResponseDTO response = sessionService.updateSession(id, updateDTO, principal, locale);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/start")
    public ResponseEntity<Session> start(@PathVariable Long id, Locale locale) {
        Session session = sessionService.startSession(id, locale);
        return ResponseEntity.status(HttpStatus.OK).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{id}/finalize")
    public ResponseEntity<Session> finalize(@PathVariable Long id, Locale locale) {
        Session session = sessionService.finalizeSession(id, locale);
        return ResponseEntity.status(HttpStatus.OK).body(session);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/{sessionId}/bugs")
    public ResponseEntity<BugDTO> reportBug(@PathVariable Long sessionId, @RequestBody BugDTO bugDto, Locale locale) {
        BugDTO response = sessionService.reportBug(sessionId, bugDto, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{sessionId}/bugs")
    public ResponseEntity<List<BugDTO>> listBugs(@PathVariable Long sessionId, Locale locale) {
        List<BugDTO> bugs = sessionService.findBugsBySession(sessionId, locale);
        return ResponseEntity.ok(bugs);
    }
}
