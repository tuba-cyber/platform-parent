package com.platform.co.company.dto;

import com.platform.co.company.entity.CompanyType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CompanyResponse {

    private UUID id;
    private String name;
    private String code;
    private String logoUrl;
    private String timezone;
    private String locale;

    // İletişim
    private String email;
    private String phone;
    private String website;

    // Adres
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String country;

    // Kurumsal
    private CompanyType companyType;
    private String companyTypeLabel;
    private String sector;
    private String taxNumber;
    private String taxOffice;
    private Integer foundedYear;
    private String description;

    // Meta
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
