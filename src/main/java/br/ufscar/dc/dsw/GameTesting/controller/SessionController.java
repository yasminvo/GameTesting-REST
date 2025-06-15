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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SessionResponseDTO>> getAllSessions(@RequestParam(defaultValue = "creationDate") String sort) {
        List<SessionResponseDTO> response = sessionService.listAllSorted(sort);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SessionResponseDTO> getSessionById(@PathVariable Long id, Principal principal) {
        return sessionService.findSessionByIdForUser(id, principal)
                .map(session -> ResponseEntity.ok(SessionResponseDTO.fromEntity(session)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    public ResponseEntity<List<SessionResponseDTO>> getSessionsByProject(@PathVariable Long projectId, Principal principal) {
        List<SessionResponseDTO> responseDTOs = sessionService.findSessionsByProjectForUser(projectId, principal);
        return ResponseEntity.ok(responseDTOs);
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity createSession(@RequestBody SessionCreateDTO createDTO, Principal principal) {
        Session novaSessao = sessionService.createSession(createDTO, principal.getName());
        SessionResponseDTO responseDTO = SessionResponseDTO.fromEntity(novaSessao);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaSessao.getId()).toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startSession(@PathVariable Long id, Principal principal) {
        try {
            Session updatedSession = sessionService.startSession(id, principal.getName());
            return ResponseEntity.ok(SessionResponseDTO.fromEntity(updatedSession));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    @PostMapping("/{id}/finalize")
    public ResponseEntity<?> finalizeSession(@PathVariable Long id, Principal principal) {
        try {
            Session updatedSession = sessionService.finalizeSession(id, principal.getName());
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
