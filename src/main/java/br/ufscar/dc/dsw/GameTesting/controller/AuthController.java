package br.ufscar.dc.dsw.GameTesting.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Controller
public class AuthController {

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

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Usuário ou senha inválidos.");
        }
        if (logout != null) {
            model.addAttribute("msg", "Logout realizado com sucesso.");
        }
        return "login";
    }

    @GetMapping("/users/redirect")
    public String redirectBasedOnRole(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/users/dashboard";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TESTER"))) {
            return "redirect:/tester/dashboard";
        }
        return "access-denied";
    }
}
