package com.example.formativa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.formativa.backend.JWTAuthenticationConfig;
import com.example.formativa.services.UserService;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTAuthenticationConfig jwtAuthtenticationConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model, CsrfToken csrf) {
        model.addAttribute("_csrf", csrf);
        return "login";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam("username") String username,
            @RequestParam("secreto") String password) {

        final UserDetails userDetails = userService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new RuntimeException("Invalid login");
        }

        String token = jwtAuthtenticationConfig.getJWTToken(username);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.add("Location", "/");

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
} 