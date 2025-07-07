package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.dtos.LoginRequestDTO;
import br.ufscar.dc.dsw.GameTesting.dtos.JwtResponseDTO;
import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.exceptions.AppException;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.repository.UserRepository;
import br.ufscar.dc.dsw.GameTesting.utils.JwtUtil;
import br.ufscar.dc.dsw.GameTesting.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            final var userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

            if (optionalUser.isEmpty()) {
                throw new AppException("Usuário não encontrado.", HttpStatus.UNAUTHORIZED);
            }

            User user = optionalUser.get();
            Role role = user.getRole();

            String token = jwtUtil.generateToken(userDetails, role);

            return ResponseEntity.ok(new JwtResponseDTO(token));
        } catch (AuthenticationException e) {
            throw new AppException("Usuário ou senha inválidos.", HttpStatus.FORBIDDEN);
        }
    }
}