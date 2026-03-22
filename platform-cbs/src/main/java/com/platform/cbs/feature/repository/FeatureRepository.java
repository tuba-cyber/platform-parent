package com.platform.cbs.feature.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.cbs.feature.entity.Feature;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, UUID> {

    List<Feature> findByLayerIdAndActiveOrderByCreatedAtDesc(
        UUID layerId, Boolean active
    );

    List<Feature> findByCompanyIdAndActive(String companyId, Boolean active);

    // Belirli bir bbox içindeki feature'ları getir
    @Query(value = """
        SELECT f FROM Feature f
        WHERE f.layer.id = :layerId
        AND f.active = true
        AND function('ST_Within', f.geometry,
            function('ST_MakeEnvelope', :minX, :minY, :maxX, :maxY, 4326)) = true
    """)
    List<Feature> findByLayerIdWithinBbox(
        @Param("layerId") UUID layerId,
        @Param("minX") double minX,
        @Param("minY") double minY,
        @Param("maxX") double maxX,
        @Param("maxY") double maxY
    );

    // Bir noktaya yakın feature'ları getir (metre cinsinden mesafe)
    @Query(value = """
        SELECT * FROM features f
        WHERE f.layer_id = :layerId
        AND f.active = true
        AND ST_DWithin(
            f.geometry::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :distanceMeters
        )
    """, nativeQuery = true)
    List<Feature> findNearbyFeatures(
        @Param("layerId") UUID layerId,
        @Param("lat") double lat,
        @Param("lon") double lon,
        @Param("distanceMeters") double distanceMeters
    );
}