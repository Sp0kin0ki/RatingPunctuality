package com.rating.punctuality.rating_punctuality.model.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Имя пользователя обязательно")
    private String username;
    
    @NotBlank(message = "Пароль обязателен")
    private String password;
}