package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.*;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import br.ufscar.dc.dsw.GameTesting.service.SessionService;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final ProjetoService projetoService;
    private final StrategyService strategyService;

    @Autowired
    public SessionController(SessionService sessionService,
                             ProjetoService projetoService,
                             StrategyService strategyService) {
        this.sessionService = sessionService;
        this.projetoService = projetoService;
        this.strategyService = strategyService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/list")
    public String getAllSessions(Model model) {
        List<SessionResponseDTO> sessions = sessionService.listAll();
        model.addAttribute("sessions", sessions);
        return "sessions/list"; // templates/sessions/list.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{sessionId}")
    public String getSessionById(@PathVariable Long sessionId, Model model) {
        SessionResponseDTO session = sessionService.findSessionById(sessionId);
        System.out.println("DEBUG session = " + session);
        model.addAttribute("sessionData", session);
        return "sessions/details"; // templates/sessions/details.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<ProjetoDTO> projetos = projetoService.listAllSorted("name");
        List<StrategyResponseDTO> estrategias = strategyService.findAll();

        model.addAttribute("projetos", projetos);
        model.addAttribute("strategias", estrategias);
        model.addAttribute("session", new SessionCreateDTO());
        return "sessions/create"; // templates/sessions/create.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/create")
    public String createSession(@ModelAttribute("session") SessionCreateDTO createDTO, Model model) {
        sessionService.createSession(createDTO);
        return "redirect:/sessions/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        SessionResponseDTO session = sessionService.findSessionById(id);
        List<StrategyResponseDTO> estrategias = strategyService.findAll();

        model.addAttribute("sessionData", session);
        model.addAttribute("strategias", estrategias);
        model.addAttribute("sessionUpdate", new SessionUpdateDTO());
        return "sessions/edit"; // templates/sessions/edit.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/edit/{id}")
    public String updateSession(@PathVariable Long id, @ModelAttribute("sessionUpdate") SessionUpdateDTO updateDTO) {
        sessionService.updateSession(id, updateDTO);
        return "redirect:/sessions/list";
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/start/{id}")
    public String startSession(@PathVariable Long id) {
        try {
            sessionService.startSession(id);
            return "redirect:/sessions/list" ;
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
    public String finalizeSession(@PathVariable Long id) {
        try {
            sessionService.finalizeSession(id);
            return "redirect:/sessions/" + id;
        } catch (AccessDeniedException e) {
            throw new AppException("Acesso negado ao finalizar sessão.", HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e) {
            throw new AppException(e.getMessage(), HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            throw new AppException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
