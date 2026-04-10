package com.platform.co.company.dto;

import com.platform.co.company.entity.CompanyType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "Şirket adı zorunludur")
    private String name;

    @NotBlank(message = "Şirket kodu zorunludur")
    @Size(min = 2, max = 20, message = "Kod 2-20 karakter olmalıdır")
    private String code;

    private String logoUrl;
    private String timezone = "Europe/Istanbul";
    private String locale = "tr";

    // İletişim
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    private String phone;
    private String website;

    // Adres
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private String country = "Türkiye";

    // Kurumsal
    private CompanyType companyType;
    private String sector;

    @Size(max = 10, message = "Vergi numarası en fazla 10 karakter olabilir")
    private String taxNumber;

    private String taxOffice;

    @Min(value = 1800, message = "Kuruluş yılı geçerli değil")
    @Max(value = 2100, message = "Kuruluş yılı geçerli değil")
    private Integer foundedYear;

    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String description;
}
