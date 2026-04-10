package com.platform.co.company.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Şirket / Tenant entity'si.
 * platform-gateway'deki Company ile aynı "companies" tablosunu paylaşır.
 * Bu modül aynı tabloya ek alanlar ekler (ddl-auto: update ile otomatik oluşur).
 */
@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company extends BaseEntity {

    // --- Temel Bilgiler (gateway ile ortak) ---
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String logoUrl;

    @Column(nullable = false)
    private String timezone = "Europe/Istanbul";

    @Column(nullable = false)
    private String locale = "tr";

    // --- İletişim Bilgileri ---
    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String website;

    // --- Adres Bilgileri ---
    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String district;

    @Column
    private String postalCode;

    @Column
    private String country = "Türkiye";

    // --- Kurumsal Bilgiler ---
    @Enumerated(EnumType.STRING)
    @Column
    private CompanyType companyType;

    @Column
    private String sector;

    @Column
    private String taxNumber;

    @Column
    private String taxOffice;

    @Column
    private Integer foundedYear;

    @Column(length = 1000)
    private String description;
}
