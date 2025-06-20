package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import br.ufscar.dc.dsw.GameTesting.utils.JwtUtil;
import br.ufscar.dc.dsw.GameTesting.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

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
        return "login"; // nome do template login.html
    }

    @PostMapping("/login")
    public String postLogin(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpServletRequest request) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            var userDetails = userDetailsService.loadUserByUsername(email);
            var user = userRepository.findByEmail(email).orElseThrow();

            Role role = user.getRole();
            String token = jwtUtil.generateToken(userDetails, role);

            request.getSession().setAttribute("token", token);

            if (role == Role.ADMIN) {
                return "redirect:/users/dashboard";
            } else {
                return "redirect:/tester-dashboard";
            }

        } catch (AuthenticationException e) {
            model.addAttribute("error", "Usuário ou senha inválidos");
            return "login";
        }
    }
}
