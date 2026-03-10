package com.platform.moduleengine.navigation.entity;

import java.util.ArrayList;
import java.util.List;

import com.platform.core.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "navigation_items")
public class NavigationItem extends BaseEntity {

    @Column(nullable = false)
    private String label;

    @Column
    private String icon;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Boolean visible = true;

    // Bağlı olduğu modül id (opsiyonel)
    @Column
    private String moduleId;

    // Bağlı olduğu ekran kodu (opsiyonel)
    @Column
    private String screenCode;

    // Dış link (opsiyonel)
    @Column
    private String externalUrl;

    // Navigasyon tipi: MODULE, SCREEN, EXTERNAL, GROUP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NavigationType navigationType;

    // Bu menüye erişmek için gereken yetki
    @Column
    private String requiredPermission;

    @Column(nullable = false)
    private String companyId;

    // Üst menü (null ise ana menü)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private NavigationItem parent;

    // Alt menüler
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<NavigationItem> children = new ArrayList<>();
}