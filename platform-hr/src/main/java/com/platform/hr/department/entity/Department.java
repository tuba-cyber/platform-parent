package com.platform.hr.department.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "hr_departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    /** Üst departman (hiyerarşi için) */
    @Column
    private UUID parentDepartmentId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
