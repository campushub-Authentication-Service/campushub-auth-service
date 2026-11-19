package com.campushub.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity> health() {
        return ResponseEntity.ok(Map.of(
                "service", "auth-service",
                "status", "UP",
                "version", "1.0.0"
        ));
    }
}