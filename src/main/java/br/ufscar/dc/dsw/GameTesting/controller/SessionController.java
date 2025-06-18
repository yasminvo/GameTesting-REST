package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.SessionCreateDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.SessionResponseDTO;
import br.ufscar.dc.dsw.GameTesting.model.Session;
import br.ufscar.dc.dsw.GameTesting.service.SessionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")    // TESTED - OK
    public ResponseEntity<?> getAllSessions() {
        List<SessionResponseDTO> response = sessionService.listAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")  // TESTED - OK
    public ResponseEntity<?> getSessionById(@PathVariable Long sessionId) {

        SessionResponseDTO responseDTO = sessionService.findSessionById(sessionId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/project/{projectId}") // TESTED - OK
    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSessionsByProject(@PathVariable Long projectId) {
        List<SessionResponseDTO> responseDTOs = sessionService.findSessionsByProjectId(projectId);
        return ResponseEntity.ok(responseDTOs);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')") // TESTED - OK
    public ResponseEntity createSession(@RequestBody SessionCreateDTO createDTO) {
        Session novaSessao = sessionService.createSession(createDTO);
        SessionResponseDTO responseDTO = SessionResponseDTO.fromEntity(novaSessao);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')") // TESTED - OK
    @PostMapping("/{sessionId}/start")
    public ResponseEntity<?> startSession(@PathVariable Long sessionId) {
        try {
            Session updatedSession = sessionService.startSession(sessionId);
            return ResponseEntity.ok(SessionResponseDTO.fromEntity(updatedSession));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/finalize")
    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')") // TESTED - OK
    public ResponseEntity<?> finalizeSession(@PathVariable Long id) {
        try {
            Session updatedSession = sessionService.finalizeSession(id);
            return ResponseEntity.ok(SessionResponseDTO.fromEntity(updatedSession));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
