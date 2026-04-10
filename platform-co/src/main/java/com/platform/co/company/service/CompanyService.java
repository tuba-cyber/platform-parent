package com.platform.co.company.service;

import com.platform.co.company.dto.CompanyRequest;
import com.platform.co.company.dto.CompanyResponse;
import com.platform.co.company.entity.Company;
import com.platform.co.company.repository.CompanyRepository;
import com.platform.core.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyResponse> getAll() {
        return companyRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CompanyResponse getById(UUID id) {
        Company company = companyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şirket bulunamadı: " + id));
        return toResponse(company);
    }

    public CompanyResponse getByCode(String code) {
        Company company = companyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Şirket bulunamadı: " + code));
        return toResponse(company);
    }

    public List<CompanyResponse> search(String keyword) {
        return companyRepository.search(keyword)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        if (companyRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Bu kod zaten kullanımda: " + request.getCode());
        }
        if (companyRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Bu şirket adı zaten kullanımda: " + request.getName());
        }

        Company company = new Company();
        applyRequest(company, request);
        Company saved = companyRepository.save(company);
        log.info("Şirket oluşturuldu: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = companyRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şirket bulunamadı: " + id));

        // Kod değişiyorsa çakışma kontrolü
        if (!company.getCode().equals(request.getCode())
                && companyRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Bu kod zaten kullanımda: " + request.getCode());
        }

        applyRequest(company, request);
        Company saved = companyRepository.save(company);
        log.info("Şirket güncellendi: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public CompanyResponse toggleActive(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şirket bulunamadı: " + id));
        company.setActive(!company.getActive());
        Company saved = companyRepository.save(company);
        log.info("Şirket durumu değiştirildi: {} → aktif={}", saved.getName(), saved.getActive());
        return toResponse(saved);
    }

    // --- Yardımcı Metotlar ---

    private void applyRequest(Company company, CompanyRequest request) {
        company.setName(request.getName());
        company.setCode(request.getCode().toUpperCase());
        company.setLogoUrl(request.getLogoUrl());
        company.setTimezone(request.getTimezone() != null ? request.getTimezone() : "Europe/Istanbul");
        company.setLocale(request.getLocale() != null ? request.getLocale() : "tr");
        company.setEmail(request.getEmail());
        company.setPhone(request.getPhone());
        company.setWebsite(request.getWebsite());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setDistrict(request.getDistrict());
        company.setPostalCode(request.getPostalCode());
        company.setCountry(request.getCountry() != null ? request.getCountry() : "Türkiye");
        company.setCompanyType(request.getCompanyType());
        company.setSector(request.getSector());
        company.setTaxNumber(request.getTaxNumber());
        company.setTaxOffice(request.getTaxOffice());
        company.setFoundedYear(request.getFoundedYear());
        company.setDescription(request.getDescription());
    }

    private CompanyResponse toResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setName(company.getName());
        response.setCode(company.getCode());
        response.setLogoUrl(company.getLogoUrl());
        response.setTimezone(company.getTimezone());
        response.setLocale(company.getLocale());
        response.setEmail(company.getEmail());
        response.setPhone(company.getPhone());
        response.setWebsite(company.getWebsite());
        response.setAddress(company.getAddress());
        response.setCity(company.getCity());
        response.setDistrict(company.getDistrict());
        response.setPostalCode(company.getPostalCode());
        response.setCountry(company.getCountry());
        response.setCompanyType(company.getCompanyType());
        if (company.getCompanyType() != null) {
            response.setCompanyTypeLabel(company.getCompanyType().getLabel());
        }
        response.setSector(company.getSector());
        response.setTaxNumber(company.getTaxNumber());
        response.setTaxOffice(company.getTaxOffice());
        response.setFoundedYear(company.getFoundedYear());
        response.setDescription(company.getDescription());
        response.setActive(company.getActive());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        return response;
    }
}
