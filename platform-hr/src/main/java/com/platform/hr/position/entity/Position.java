package com.platform.hr.position.entity;

import com.platform.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "hr_positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position extends BaseEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 50, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    /** Departman bağlantısı (opsiyonel) */
    @Column
    private UUID departmentId;

    /** Pozisyon seviyesi (örn. Junior, Mid, Senior, Manager) */
    @Column(length = 100)
    private String level;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
