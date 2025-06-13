package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/testers")
@PreAuthorize("hasRole('ADMIN')")
public class TesterController {

    private final UserService userService;

    public TesterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> getAllTesters() {
        return userService.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.TESTER)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getTesterById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.filter(u -> u.getRole() == Role.TESTER)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<User> createTester(@RequestBody User user) {
        user.setRole(Role.TESTER);
        User createdUser = userService.save(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateTester(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isEmpty() || existingUser.get().getRole() != Role.TESTER) {
            return ResponseEntity.notFound().build();
        }

        User user = existingUser.get();
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(Role.TESTER);

        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTester(@PathVariable Long id) {
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isEmpty() || existingUser.get().getRole() != Role.TESTER) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
