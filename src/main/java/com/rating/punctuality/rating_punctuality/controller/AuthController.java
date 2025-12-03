package com.rating.punctuality.rating_punctuality.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.rating.punctuality.rating_punctuality.model.auth.LoginRequest;
import com.rating.punctuality.rating_punctuality.model.auth.UserRegistrationDto;
import com.rating.punctuality.rating_punctuality.model.entities.User;
import com.rating.punctuality.rating_punctuality.services.AuthService;
import com.rating.punctuality.rating_punctuality.services.JwtService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService; // Ваш существующий сервис

    /**
     * Вход пользователя
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Попытка входа: {}", loginRequest.getUsername());
        
        try {
            // 1. Аутентифицируем пользователя (проверяем логин/пароль)
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // 2. Получаем UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 3. Генерируем JWT токен
            String token = jwtService.generateToken(userDetails);
            
            // 4. Формируем ответ
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            response.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            response.put("message", "Вход выполнен успешно");
            
            log.info("Успешный вход: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Ошибка входа для {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Неверное имя пользователя или пароль"));
        }
    }

    /**
     * Регистрация нового пользователя
     * POST /api/auth/add-user
     */
    @PostMapping("/add-user")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        log.info("Регистрация пользователя: {}", userRegistrationDto.getUsername());
        
        try {
            // 1. Регистрируем пользователя (ваш существующий метод)
            User registeredUser = 
                authService.register(userRegistrationDto);
            
            // 2. Автоматически аутентифицируем нового пользователя
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    userRegistrationDto.getUsername(),
                    userRegistrationDto.getPassword()
                )
            );
            
            // 3. Генерируем токен
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            // 4. Формируем ответ
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", registeredUser.getUsername());
            response.put("email", registeredUser.getEmail());
            response.put("message", "Пользователь успешно зарегистрирован");
            
            log.info("Успешная регистрация: {}", registeredUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Ошибка регистрации: " + e.getMessage()));
        }
    }

    /**
     * Проверка токена / информация о текущем пользователе
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Не авторизован"));
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        response.put("authenticated", true);
        
        return ResponseEntity.ok(response);
    }
}