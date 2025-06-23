package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.CreateStrategyDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.StrategyResponseDTO;
import br.ufscar.dc.dsw.GameTesting.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/strategies")
public class StrategyController {

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

    private final StrategyService strategyService;

    @Autowired
    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("strategy", new CreateStrategyDTO());
        return "strategies/create"; // templates/strategies/create.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/create")
    public String createStrategy(@ModelAttribute("strategy") CreateStrategyDTO strategyDTO) {
        strategyService.save(strategyDTO);
        return "redirect:/strategies/list";
    }

    @GetMapping("/list")
    public String listStrategies(Model model) {
        List<StrategyResponseDTO> strategies = strategyService.findAll();
        model.addAttribute("strategies", strategies);
        return "strategies/list"; // templates/strategies/list.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StrategyResponseDTO strategy = strategyService.findById(id);
        model.addAttribute("strategy", strategy);
        return "strategies/edit"; // templates/strategies/edit.html
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/edit/{id}")
    public String updateStrategy(@PathVariable Long id,
                                 @ModelAttribute("strategy") CreateStrategyDTO dto) {
        strategyService.update(id, dto);
        return "redirect:/strategies/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @GetMapping("/{id}")
    public String fingStrategy(@PathVariable Long id, Model model) {
        StrategyResponseDTO strategyDTO = strategyService.findById(id);
        model.addAttribute("strategy", strategyDTO);
        return "strategies/detail";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
    @PostMapping("/delete/{id}")
    public String deleteStrategy(@PathVariable Long id) {
        strategyService.delete(id);
        return "redirect:/strategies/list";
    }

}