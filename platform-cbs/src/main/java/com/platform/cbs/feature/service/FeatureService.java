package com.platform.cbs.feature.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.cbs.feature.dto.FeatureRequest;
import com.platform.cbs.feature.dto.FeatureResponse;
import com.platform.cbs.feature.dto.GeoJsonFeature;
import com.platform.cbs.feature.dto.GeoJsonFeatureCollection;
import com.platform.cbs.feature.entity.Feature;
import com.platform.cbs.feature.repository.FeatureRepository;
import com.platform.cbs.layer.entity.Layer;
import com.platform.cbs.layer.repository.LayerRepository;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureService {

    private final FeatureRepository featureRepository;
    private final LayerRepository layerRepository;

    private final GeoJsonReader geoJsonReader = new GeoJsonReader();

    @Transactional
    public FeatureResponse create(FeatureRequest request) throws Exception {
        Layer layer = layerRepository.findById(
                UUID.fromString(request.getLayerId()))
            .orElseThrow(() -> new ResourceNotFoundException(
                "Katman", request.getLayerId()));

        String companyId = getCurrentCompanyId();

        Geometry geometry = geoJsonReader.read(request.getGeometryGeoJson());
        geometry.setSRID(4326);

        Feature feature = new Feature();
        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        feature.setFeatureType(request.getFeatureType());
        feature.setGeometry(geometry);
        feature.setProperties(request.getProperties());
        feature.setCompanyId(companyId);
        feature.setLayer(layer);

        featureRepository.save(feature);
        log.info("Feature oluşturuldu: {}", feature.getName());

        return toResponse(feature);
    }

    @Transactional(readOnly = true)
    public GeoJsonFeatureCollection getByLayerId(UUID layerId) {
        List<Feature> features = featureRepository
            .findByLayerIdAndActiveOrderByCreatedAtDesc(layerId, true);

        List<GeoJsonFeature> geoJsonFeatures = features.stream()
            .map(this::toGeoJsonFeature)
            .collect(Collectors.toList());

        return GeoJsonFeatureCollection.builder()
                .features(geoJsonFeatures)
                .totalCount(geoJsonFeatures.size())
                .build();
    }

    @Transactional(readOnly = true)
    public GeoJsonFeatureCollection getByBbox(
            UUID layerId, double minX, double minY,
            double maxX, double maxY) {

        List<Feature> features = featureRepository
            .findByLayerIdWithinBbox(layerId, minX, minY, maxX, maxY);

        List<GeoJsonFeature> geoJsonFeatures = features.stream()
            .map(this::toGeoJsonFeature)
            .collect(Collectors.toList());

        return GeoJsonFeatureCollection.builder()
                .features(geoJsonFeatures)
                .totalCount(geoJsonFeatures.size())
                .build();
    }

    @Transactional(readOnly = true)
    public GeoJsonFeatureCollection getNearby(
            UUID layerId, double lat, double lon,
            double distanceMeters) {

        List<Feature> features = featureRepository
            .findNearbyFeatures(layerId, lat, lon, distanceMeters);

        List<GeoJsonFeature> geoJsonFeatures = features.stream()
            .map(this::toGeoJsonFeature)
            .collect(Collectors.toList());

        return GeoJsonFeatureCollection.builder()
                .features(geoJsonFeatures)
                .totalCount(geoJsonFeatures.size())
                .build();
    }

    @Transactional(readOnly = true)
    public FeatureResponse getById(UUID id) {
        Feature feature = featureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature", id));
        return toResponse(feature);
    }

    @Transactional
    public FeatureResponse update(UUID id, FeatureRequest request)
            throws Exception {
        Feature feature = featureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature", id));

        Geometry geometry = geoJsonReader.read(request.getGeometryGeoJson());
        geometry.setSRID(4326);

        feature.setName(request.getName());
        feature.setDescription(request.getDescription());
        feature.setFeatureType(request.getFeatureType());
        feature.setGeometry(geometry);
        feature.setProperties(request.getProperties());

        featureRepository.save(feature);
        log.info("Feature güncellendi: {}", feature.getName());

        return toResponse(feature);
    }

    @Transactional
    public void delete(UUID id) {
        Feature feature = featureRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Feature", id));
        feature.setActive(false);
        featureRepository.save(feature);
        log.info("Feature pasif yapıldı: {}", feature.getName());
    }

    // --- Yardımcı Metodlar ---

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    private String geometryToGeoJson(Geometry geometry) {
        if (geometry == null) return null;
        try {
            String type = geometry.getGeometryType();
            return "{\"type\":\"" + type + "\",\"coordinates\":"
                + buildCoordinates(type, geometry) + "}";
        } catch (Exception e) {
            log.error("Geometry GeoJSON'a çevrilemedi: {}", e.getMessage());
            return null;
        }
    }

    private String buildCoordinates(String type, Geometry geometry) {
        StringBuilder sb = new StringBuilder();
        if (type.equals("Point")) {
            org.locationtech.jts.geom.Coordinate c = geometry.getCoordinate();
            sb.append("[").append(c.x).append(",").append(c.y).append("]");
        } else if (type.equals("LineString") || type.equals("MultiPoint")) {
            sb.append("[");
            for (org.locationtech.jts.geom.Coordinate c :
                    geometry.getCoordinates()) {
                sb.append("[").append(c.x).append(",")
                  .append(c.y).append("],");
            }
            if (sb.charAt(sb.length() - 1) == ',')
                sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        } else if (type.equals("Polygon")) {
            sb.append("[[");
            for (org.locationtech.jts.geom.Coordinate c :
                    geometry.getCoordinates()) {
                sb.append("[").append(c.x).append(",")
                  .append(c.y).append("],");
            }
            if (sb.charAt(sb.length() - 1) == ',')
                sb.deleteCharAt(sb.length() - 1);
            sb.append("]]");
        } else {
            sb.append("[]");
        }
        return sb.toString();
    }

    public FeatureResponse toResponse(Feature feature) {
        return FeatureResponse.builder()
                .id(feature.getId())
                .name(feature.getName())
                .description(feature.getDescription())
                .featureType(feature.getFeatureType())
                .geometryGeoJson(geometryToGeoJson(feature.getGeometry()))
                .properties(feature.getProperties())
                .active(feature.getActive())
                .companyId(feature.getCompanyId())
                .layerId(feature.getLayer().getId())
                .createdAt(feature.getCreatedAt())
                .updatedAt(feature.getUpdatedAt())
                .build();
    }

    private GeoJsonFeature toGeoJsonFeature(Feature feature) {
        Map<String, Object> props = new HashMap<>();
        props.put("id", feature.getId().toString());
        props.put("name", feature.getName());
        props.put("description", feature.getDescription());
        props.put("featureType", feature.getFeatureType());
        props.put("layerId", feature.getLayer().getId().toString());
        if (feature.getProperties() != null) {
            props.put("customProperties", feature.getProperties());
        }

        return GeoJsonFeature.builder()
                .id(feature.getId().toString())
                .geometry(geometryToGeoJson(feature.getGeometry()))
                .properties(props)
                .build();
    }
}