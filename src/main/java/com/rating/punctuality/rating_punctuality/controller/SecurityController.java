package com.rating.punctuality.rating_punctuality.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rating.punctuality.filters.ApiKeyAuthFilter;
import com.rating.punctuality.rating_punctuality.model.external.Company;
import com.rating.punctuality.rating_punctuality.model.external.UploadData;
import com.rating.punctuality.rating_punctuality.repository.external.CompanyRepository;
import com.rating.punctuality.rating_punctuality.repository.external.UploadDataRepository;
import com.rating.punctuality.rating_punctuality.service.TokenService;

@RestController()
@RequestMapping("/company")
public class SecurityController {

    private UploadDataRepository uploadDataRepository;
    private CompanyRepository companyRepository;
    private final TokenService tokenService;
    private final ApiKeyAuthFilter apiKeyAuthFilter;

    public SecurityController(UploadDataRepository uploadDataRepository, CompanyRepository companyRepository,
            TokenService tokenService, ApiKeyAuthFilter apiKeyAuthFilter) {
        this.uploadDataRepository = uploadDataRepository;
        this.companyRepository = companyRepository;
        this.tokenService = tokenService;
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(
            @RequestBody List<UploadData> data) {
        uploadDataRepository.saveAll(data);
        return ResponseEntity.ok("Данные загружены");
    }

    public static class CompanyResponse {
        private Long id;
        private String nameCompany;
        private String token;
        private String message;

        public CompanyResponse() {
        }

        public CompanyResponse(Long id, String nameCompany, String token, String message) {
            this.id = id;
            this.nameCompany = nameCompany;
            this.token = token;
            this.message = message;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNameCompany() {
            return nameCompany;
        }

        public void setNameCompany(String nameCompany) {
            this.nameCompany = nameCompany;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("/create-test-company")
    public ResponseEntity<CompanyResponse> createTestCompany() {

        String rawToken = tokenService.generateToken();

        Company company = new Company();
        company.setNameCompany("TestAirline");
        company.setApiTokenHash(tokenService.hashToken(rawToken));
        company.setActive(true);

        Company saved = companyRepository.save(company);

        CompanyResponse response = new CompanyResponse(
                saved.getId(),
                saved.getNameCompany(),
                rawToken, // Токен показывается только один раз при создании
                "Save this token securely! It won't be shown again.");

        return ResponseEntity.ok(response);
    }
}
