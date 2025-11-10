package com.rating.punctuality.rating_punctuality.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final PasswordEncoder passwordEncoder;

    public TokenService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String hashToken(String rawToken) {
        return passwordEncoder.encode(rawToken);
    }

    public boolean verifyToken(String rawToken, String storedHash) {
        return passwordEncoder.matches(rawToken, storedHash);
    }
    
}
