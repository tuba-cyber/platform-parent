package com.platform.gateway.company.entity;

import com.platform.core.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String logoUrl;

    @Column
    private String address;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(nullable = false)
    private String timezone = "Europe/Istanbul";

    @Column(nullable = false)
    private String locale = "tr";
}