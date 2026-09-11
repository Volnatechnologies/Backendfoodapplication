package com.caloryhive.business.staff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getRoot() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "Staff Management Backend API is running");
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
}
