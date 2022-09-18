package com.gucardev.jwtauthentication.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class HomeController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "public";
    }

    @GetMapping("/auth-required")
    public String testEndpoint() {
        return "auth-required";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/me")
    public Map<String, String> admin(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("credientals", String.format("User: %s, Role: %s", authentication.getName(), authentication.getAuthorities().toString()));
        return response;
    }

}
