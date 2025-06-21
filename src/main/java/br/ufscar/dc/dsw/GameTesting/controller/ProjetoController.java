package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.ProjetoDTO;
import br.ufscar.dc.dsw.GameTesting.service.ProjetoService;
import br.ufscar.dc.dsw.GameTesting.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import br.ufscar.dc.dsw.GameTesting.dtos.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final UserService userService;

    public ProjetoController(ProjetoService projetoService, UserService userService) {
        this.projetoService = projetoService;
        this.userService = userService;
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
        List<ProjetoDTO> projetos = projetoService.listAllSorted(sort);
        model.addAttribute("projetos", projetos);
        return "projects/view-tester";
    }

    @PreAuthorize("hasRole('TESTER') or hasRole('ADMIN')")
    @GetMapping("/{id:\\d+}")
    public String viewProject(@PathVariable Long id, Model model) {
        Optional<ProjetoDTO> projetoOpt = projetoService.getById(id);
        if (projetoOpt.isEmpty()) {
            return "redirect:/projects";
        }
        model.addAttribute("projeto", projetoOpt.get());
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
        Optional<ProjetoDTO> projetoOpt = projetoService.getById(id);
        if (projetoOpt.isEmpty()) {
            return "redirect:/projects";
        }
        ProjetoDTO projetoDTO = projetoOpt.get();

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
