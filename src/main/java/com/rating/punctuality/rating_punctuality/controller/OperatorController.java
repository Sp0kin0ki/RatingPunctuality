package com.rating.punctuality.rating_punctuality.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operator")
public class OperatorController {

    @GetMapping("/diagnostics")
    @PreAuthorize("hasRole('OPERATOR')")
    public String diagnostics() {
        return "Operator diagnostics: OK";
    }
}