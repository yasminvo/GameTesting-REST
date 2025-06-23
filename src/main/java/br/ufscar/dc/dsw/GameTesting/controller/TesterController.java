package br.ufscar.dc.dsw.GameTesting.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/tester")
@PreAuthorize("hasRole('TESTER')")
public class TesterController {

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

    @GetMapping("/dashboard")
    public String showTesterDashboard() {
        return "tester/dashboard";
    }

}