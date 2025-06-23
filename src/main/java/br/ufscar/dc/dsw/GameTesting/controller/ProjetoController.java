package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.ProjetoDTO;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import br.ufscar.dc.dsw.GameTesting.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import br.ufscar.dc.dsw.GameTesting.dtos.UserDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjetoController {

    @ModelAttribute("module")
    public Map<String, String> module() {
        Map<String, String> module = new HashMap<>();
        module.put("user", "Usuário");
        module.put("project", "Projeto");
        module.put("strategy", "Estratégia");
        module.put("session", "Sessão");
        module.put("tester", "Tester");
        module.put("admin", "Administrador");
        return module;
    }

    private final ProjetoService projetoService;
    private final UserService userService;
    private final UserRepository userRepository;

    public ProjetoController(ProjetoService projetoService, UserService userService, UserRepository userRepository) {
        this.projetoService = projetoService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String listProjects(@RequestParam(defaultValue = "creationDate") String sort, Model model) {
        List<ProjetoDTO> projetos = projetoService.listAllSorted(sort);
        model.addAttribute("projetos", projetos);
        return "projects/list";
    }

    @PreAuthorize("hasRole('TESTER')")
    @GetMapping("/view-tester")
    public String viewProjects(@RequestParam(defaultValue = "creationDate") String sort, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Usuario não encontrado.", HttpStatus.NOT_FOUND));

        Long testerId = user.getId();
        List<ProjetoDTO> projetos = projetoService.listByTesterIdSorted(testerId, sort);
        model.addAttribute("projetos", projetos);
        return "projects/view-tester";
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    @GetMapping("/{id:\\d+}")
    public String viewProject(@PathVariable Long id, Model model) {
        ProjetoDTO projetoOpt = projetoService.getById(id);

        model.addAttribute("projeto", projetoOpt);
        return "projects/detail";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projeto", new ProjetoDTO());
        model.addAttribute("usuarios", userService.findAll());
        return "projects/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("projeto") ProjetoDTO projetoDTO,
                                @RequestParam(value = "memberIds", required = false) List<Long> memberIds,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "projects/create";
        }

        projetoDTO.setMembers(userService.getUsersByIds(memberIds));
        projetoService.createProjeto(projetoDTO);
        return "redirect:/projects/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProjetoDTO projetoDTO = projetoService.getById(id);

        projetoDTO.setMemberIds(
                projetoDTO.getMembers().stream()
                        .map(UserDTO::getId)
                        .collect(Collectors.toList())
        );

        model.addAttribute("projeto", projetoDTO);
        model.addAttribute("usuarios", userService.findAll());
        return "projects/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateProject(@PathVariable Long id,
                                @Valid @ModelAttribute("projeto") ProjetoDTO projetoDTO,
                                @RequestParam(value = "memberIds", required = false) List<Long> memberIds,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "projects/edit";
        }

        projetoDTO.setMembers(userService.getUsersByIds(memberIds));
        projetoService.updateProjeto(id, projetoDTO);
        return "redirect:/projects/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projetoService.deleteProjeto(id);
        return "redirect:/projects/list";
    }
}
