package com.platform.co.branch.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "company_branches")
@Getter
@Setter
@NoArgsConstructor
public class Branch extends BaseEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String district;

    @Column
    private String postalCode;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(nullable = false)
    private Boolean headquarters = false;
}
