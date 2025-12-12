package com.example.multitenant.controller;

import com.example.multitenant.repo.AdminRepository;
import com.example.multitenant.model.AdminUser;
import com.example.multitenant.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AuthController {
    private final AdminRepository adminRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(AdminRepository adminRepo, BCryptPasswordEncoder encoder, JwtUtil jwtUtil){
        this.adminRepo = adminRepo; this.encoder = encoder; this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body){
        String email = body.get("email"), password = body.get("password");
        return adminRepo.findByEmail(email).map(admin -> {
            if (encoder.matches(password, admin.getPasswordHash())) {
                String token = jwtUtil.generateToken(admin.getId(), admin.getOrganizationId(), admin.getEmail());
                return ResponseEntity.ok(Map.of("token", token, "orgId", admin.getOrganizationId()));
            } else return ResponseEntity.status(401).body("unauthorized");
        }).orElse(ResponseEntity.status(401).body("unauthorized"));
    }
}
