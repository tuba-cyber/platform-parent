package com.platform.moduleengine.screen.entity;

import com.platform.core.common.entity.BaseEntity;
import com.platform.moduleengine.section.entity.Section;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "screens")
public class Screen extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String description;

    @Column
    private String icon;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Boolean visible = true;

    // Ekran şablonu JSON olarak saklanır
    @Column(columnDefinition = "TEXT")
    private String template;

    // Ekran tipi: LIST, FORM, DETAIL, MAP, DASHBOARD, CUSTOM
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreenType screenType = ScreenType.CUSTOM;

    // Bu ekrana erişmek için gereken yetki
    @Column
    private String requiredPermission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
}