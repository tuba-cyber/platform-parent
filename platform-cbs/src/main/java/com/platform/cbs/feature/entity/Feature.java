package com.platform.cbs.feature.entity;

import org.locationtech.jts.geom.Geometry;

import com.platform.cbs.layer.entity.Layer;
import com.platform.core.common.entity.BaseEntity;

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
@Table(name = "features")
public class Feature extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeatureType featureType;

    // PostGIS geometri alanı
    @Column(columnDefinition = "geometry(Geometry, 4326)")
    private Geometry geometry;

    // Özellikler (JSON olarak saklanır)
    @Column(columnDefinition = "TEXT")
    private String properties;

    @Column(nullable = false)
    private String companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layer_id", nullable = false)
    private Layer layer;
}