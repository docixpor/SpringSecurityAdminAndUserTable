package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String printUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user-list";
    }

    @GetMapping("/user-create")
    public String createUserForm() {
        return "user-create";
    }

    @PostMapping
    public String addUser(User user, @RequestParam("roles") List<Long> roleIds) {
        Set<Role> userRoles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleService.findById(roleId);
            userRoles.add(role);
        }
        user.setRoles(userRoles);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/user-delete/{id}")
    public String deleteUserFromTable(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @PatchMapping("/user-update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "user-update";
    }

    @PatchMapping("/user-update")
    public String updateUser(User user, @RequestParam("roles") List<Long> roleIds) {
        Set<Role> userRoles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleService.findById(roleId);
            userRoles.add(role);
        }
        user.setRoles(userRoles);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/info")
    public String Info(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("currentUser", userService.findByUsername(userDetails.getUsername()));
        return "redirect:/user";
    }
}
