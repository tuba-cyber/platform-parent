package com.platform.cbs.layer.entity;

import com.platform.core.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "layers")
public class Layer extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LayerType layerType;

    // GeoServer bağlantı bilgileri
    private String geoserverWorkspace;
    private String geoserverLayerName;
    private String wmsUrl;
    private String wfsUrl;

    // Stil
    private String defaultStyle;
    private String color;
    private String icon;

    @Column(nullable = false)
    private Integer orderIndex = 0;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false)
    private Boolean queryable = true;

    // Yetki
    private String requiredPermission;

    @Column(nullable = false)
    private String companyId;
}