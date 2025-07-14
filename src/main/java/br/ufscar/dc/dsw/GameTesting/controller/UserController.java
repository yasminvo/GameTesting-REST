package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAll(Locale locale) {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id, Locale locale) {
        User user = userService.findById(id, locale);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/admins")
    public ResponseEntity<User> createAdmin(@RequestBody User user, Locale locale) {
        User response = userService.create(user, Role.ADMIN, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/testers")
    public ResponseEntity<User> createTester(@RequestBody User user, Locale locale) {
        User response = userService.create(user, Role.TESTER, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User userDetails, Locale locale) {
        User response = userService.update(id, userDetails, locale);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Locale locale) {
        userService.delete(id, locale);
        return ResponseEntity.noContent().build();
    }
}
