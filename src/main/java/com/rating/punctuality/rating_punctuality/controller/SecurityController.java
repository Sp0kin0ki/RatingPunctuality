package com.rating.punctuality.rating_punctuality.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rating.punctuality.rating_punctuality.model.external.Company;
import com.rating.punctuality.rating_punctuality.model.external.UploadData;
import com.rating.punctuality.rating_punctuality.repository.external.CompanyRepository;
import com.rating.punctuality.rating_punctuality.repository.external.UploadDataRepository;

@RestController()
@RequestMapping("/company")
public class SecurityController {
    
    private UploadDataRepository uploadDataRepository;
    private CompanyRepository companyRepository;

    public SecurityController(UploadDataRepository uploadDataRepository, CompanyRepository companyRepository) {
        this.uploadDataRepository = uploadDataRepository;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(
        @RequestBody List<UploadData> data
    ) {
        uploadDataRepository.saveAll(data);
        return ResponseEntity.ok("Данные загружены");
    }

    @PostMapping("/create-test-company")
    public ResponseEntity<Company> createTestCompany() {
        Company company = new Company();
        company.setNameCompany("TestAirline");
        company.setApiToken(java.util.UUID.randomUUID().toString().replace("-", ""));
        company.setActive(true);
        
        Company saved = companyRepository.save(company);
        return ResponseEntity.ok(saved);
    }
}
