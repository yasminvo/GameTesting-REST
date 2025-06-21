package br.ufscar.dc.dsw.GameTesting.controller;

import br.ufscar.dc.dsw.GameTesting.enums.Role;
import br.ufscar.dc.dsw.GameTesting.model.User;
import br.ufscar.dc.dsw.GameTesting.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN') or hasRole('TESTER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "users/dashboard";
    }


    @GetMapping("list")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/{id:\\d+}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/detail";
    }

    @GetMapping("/create-admin")
    public String showCreateAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create-admin";
    }

    @PostMapping("/create-admin")
    public String createAdmin(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/create-admin";
        }
        userService.create(user, Role.ADMIN);
        return "redirect:/users";
    }

    @GetMapping("/create-tester")
    public String showCreateTesterForm(Model model) {
        model.addAttribute("user", new User());
        return "users/create-tester";
    }

    @PostMapping("/create-tester")
    public String createTester(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/create-tester";
        }
        userService.create(user, Role.TESTER);
        return "redirect:/users/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/edit"; // template users/edit.html
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "users/edit";
        }
        userService.update(id, user);
        return "redirect:/users/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/users/list";
    }
}

