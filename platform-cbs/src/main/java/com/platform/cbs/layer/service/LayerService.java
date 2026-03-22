package com.platform.cbs.layer.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.cbs.layer.dto.LayerRequest;
import com.platform.cbs.layer.dto.LayerResponse;
import com.platform.cbs.layer.entity.Layer;
import com.platform.cbs.layer.repository.LayerRepository;
import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LayerService {

    private final LayerRepository layerRepository;

    @Transactional
    public LayerResponse create(LayerRequest request) {
        if (layerRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu katman kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        String companyId = getCurrentCompanyId();

        Layer layer = new Layer();
        layer.setCode(request.getCode());
        layer.setName(request.getName());
        layer.setDescription(request.getDescription());
        layer.setLayerType(request.getLayerType());
        layer.setGeoserverWorkspace(request.getGeoserverWorkspace());
        layer.setGeoserverLayerName(request.getGeoserverLayerName());
        layer.setWmsUrl(request.getWmsUrl());
        layer.setWfsUrl(request.getWfsUrl());
        layer.setDefaultStyle(request.getDefaultStyle());
        layer.setColor(request.getColor());
        layer.setIcon(request.getIcon());
        layer.setOrderIndex(request.getOrderIndex());
        layer.setVisible(request.getVisible());
        layer.setQueryable(request.getQueryable());
        layer.setRequiredPermission(request.getRequiredPermission());
        layer.setCompanyId(companyId);

        layerRepository.save(layer);
        log.info("Katman oluşturuldu: {}", layer.getCode());

        return toResponse(layer);
    }

    @Transactional(readOnly = true)
    public List<LayerResponse> getAll() {
        String companyId = getCurrentCompanyId();
        return layerRepository
            .findByCompanyIdAndActiveOrderByOrderIndexAsc(companyId, true)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LayerResponse getById(UUID id) {
        Layer layer = layerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Katman", id));
        return toResponse(layer);
    }

    @Transactional
    public LayerResponse update(UUID id, LayerRequest request) {
        Layer layer = layerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Katman", id));

        if (!layer.getCode().equals(request.getCode()) &&
            layerRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu katman kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        layer.setCode(request.getCode());
        layer.setName(request.getName());
        layer.setDescription(request.getDescription());
        layer.setLayerType(request.getLayerType());
        layer.setGeoserverWorkspace(request.getGeoserverWorkspace());
        layer.setGeoserverLayerName(request.getGeoserverLayerName());
        layer.setWmsUrl(request.getWmsUrl());
        layer.setWfsUrl(request.getWfsUrl());
        layer.setDefaultStyle(request.getDefaultStyle());
        layer.setColor(request.getColor());
        layer.setIcon(request.getIcon());
        layer.setOrderIndex(request.getOrderIndex());
        layer.setVisible(request.getVisible());
        layer.setQueryable(request.getQueryable());
        layer.setRequiredPermission(request.getRequiredPermission());

        layerRepository.save(layer);
        log.info("Katman güncellendi: {}", layer.getCode());

        return toResponse(layer);
    }

    @Transactional
    public void delete(UUID id) {
        Layer layer = layerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Katman", id));
        layer.setActive(false);
        layerRepository.save(layer);
        log.info("Katman pasif yapıldı: {}", layer.getCode());
    }

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    public LayerResponse toResponse(Layer layer) {
        return LayerResponse.builder()
                .id(layer.getId())
                .code(layer.getCode())
                .name(layer.getName())
                .description(layer.getDescription())
                .layerType(layer.getLayerType())
                .geoserverWorkspace(layer.getGeoserverWorkspace())
                .geoserverLayerName(layer.getGeoserverLayerName())
                .wmsUrl(layer.getWmsUrl())
                .wfsUrl(layer.getWfsUrl())
                .defaultStyle(layer.getDefaultStyle())
                .color(layer.getColor())
                .icon(layer.getIcon())
                .orderIndex(layer.getOrderIndex())
                .visible(layer.getVisible())
                .queryable(layer.getQueryable())
                .active(layer.getActive())
                .requiredPermission(layer.getRequiredPermission())
                .companyId(layer.getCompanyId())
                .createdAt(layer.getCreatedAt())
                .updatedAt(layer.getUpdatedAt())
                .build();
    }
}