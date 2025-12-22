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
import org.springframework.web.multipart.MultipartFile;

import com.rating.punctuality.rating_punctuality.model.auth.LoginRequest;
import com.rating.punctuality.rating_punctuality.model.auth.UserRegistrationDto;
import com.rating.punctuality.rating_punctuality.model.entities.User;
import com.rating.punctuality.rating_punctuality.model.entities.UserFiles;
import com.rating.punctuality.rating_punctuality.model.enums.UserRoles;
import com.rating.punctuality.rating_punctuality.model.entities.Flight;
import com.rating.punctuality.rating_punctuality.services.AuthService;
import com.rating.punctuality.rating_punctuality.services.JwtService;
import com.rating.punctuality.rating_punctuality.services.UploadDataService;
import com.rating.punctuality.rating_punctuality.services.UserFilesService;
import com.rating.punctuality.rating_punctuality.utils.DockerRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;
    private final UploadDataService uploadDataService;
    private final UserFilesService userFilesService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Попытка входа: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

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
            log.warn("Ошибка входа: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неверное имя пользователя или пароль"));
        }
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        log.info("Регистрация пользователя: {}", userRegistrationDto.getUsername());

        try {
            User registeredUser = authService.register(userRegistrationDto);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRegistrationDto.getUsername(),
                            userRegistrationDto.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

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

    @PostMapping("/upload-data")
    public ResponseEntity<?> uploadData(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Файл пустой");
            }

            String username = authentication.getName();
            log.info("Пользователь {} загружает файл: {}", username, file.getOriginalFilename());

            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("Только CSV файлы разрешены");
            }

            List<Flight> csvData = uploadDataService.parseCsvFileSimple(file);

            boolean isSave = uploadDataService.saveFlights(csvData);

            Map<String, Object> response = new HashMap<>();
            HttpStatus status;

            if (isSave) {
                status = HttpStatus.OK;
                response.put("message", "Файл успешно загружен");
                response.put("filename", file.getOriginalFilename());
                response.put("uploadedBy", username);
                response.put("rowsProcessed", csvData.size());
                log.info("Пользователь {} успешно загрузил {} рейсов", username, csvData.size());

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                
                UserFiles userFile = new UserFiles();
                userFile.setUserId(authService.getUser(userDetails.getUsername()).getId());
                userFile.setFileName(file.getOriginalFilename());
                userFile.setFileSize(file.getSize());
                userFile.setUploadDate(LocalDateTime.now());

                UserFiles savedFile = userFilesService.saveFile(userFile);
                log.info("Сохранена информация о файле {} для пользователя {}",
                        savedFile.getFileName(), username);

                DockerRunner.runContainer();
            } else {
                status = HttpStatus.BAD_REQUEST;
                response.put("message", "Файл не загружен (ошибка сохранения)");
                response.put("filename", file.getOriginalFilename());
                response.put("uploadedBy", username);
                response.put("rowsProcessed", csvData.size());
                log.error("Ошибка сохранения файла от пользователя {}", username);
            }

            return ResponseEntity.status(status).body(response);

        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при обработке файла: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Ошибка обработки: " + e.getMessage());
        }

    }

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
        response.put("description", authService.getUser(userDetails.getUsername()).getDescription());
        response.put("email", authService.getUser(userDetails.getUsername()).getEmail());
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-users")
    public ResponseEntity<?> getCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Не авторизован"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("countUsers", authService.getCountUsers(UserRoles.USER));
        response.put("countAdmin", authService.getCountUsers(UserRoles.ADMIN));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-files")
    public ResponseEntity<?> getAllUserFiles(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Не авторизован"));
        }

        Map<String, Object> response = new HashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        response.put("userFiles", userFilesService.getAllUserFiles(authService.getUser(userDetails.getUsername()).getId()));
        return ResponseEntity.ok(response);
    }
}