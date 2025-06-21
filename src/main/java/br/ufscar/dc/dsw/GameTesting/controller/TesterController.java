package br.ufscar.dc.dsw.GameTesting.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tester")
@PreAuthorize("hasRole('TESTER')")
public class TesterController {

    @GetMapping("/dashboard")
    public String showTesterDashboard() {
        return "tester/dashboard";
    }

}