package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    private UserServiceImpl userService;
    private RoleRepository roleRepository;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void RoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping("/user")
    public String printUser(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("currentUser", userService.findByUsername(userDetails.getUsername()));
        return "user";
    }

    @GetMapping("/admin")
    public String printUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user-list";
    }

    @GetMapping("/admin/user-create")
    public String createUserForm() {
        return "user-create";
    }

    @PostMapping("/user-create")
    public String addUser(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.getRoles().add(userRole);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user-delete/{id}")
    public String deleteUserFromTable(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user-update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user-update";
    }

    @PostMapping("/admin/user-update")
    public String updateUser(User user) {
        Role userRole = roleRepository.findByName("ROLE_USER");
        user.getRoles().add(userRole);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/info")
    public String Info(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("currentUser", userService.findByUsername(userDetails.getUsername()));
        return "redirect:/user";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            request.getSession().invalidate();
        }
        return "redirect:/login";
    }
}
