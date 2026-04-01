package com.platform.cbs.layer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.platform.cbs.layer.dto.LayerRequest;
import com.platform.cbs.layer.dto.LayerResponse;
import com.platform.cbs.layer.entity.Layer;
import com.platform.cbs.layer.entity.LayerType;
import com.platform.cbs.layer.repository.LayerRepository;
import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class LayerServiceTest {

    @Mock
    private LayerRepository layerRepository;

    @InjectMocks
    private LayerService layerService;

    private UserPrincipal testPrincipal;
    private Layer testLayer;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("")
                .companyId("company1")
                .roles(List.of("ADMIN"))
                .permissions(List.of("LAYER_MANAGE"))
                .active(true)
                .build();

        testLayer = new Layer();
        testLayer.setId(UUID.randomUUID());
        testLayer.setCode("BUILDINGS");
        testLayer.setName("Binalar");
        testLayer.setDescription("Bina katmanı");
        testLayer.setLayerType(LayerType.VECTOR);
        testLayer.setGeoserverWorkspace("platform");
        testLayer.setGeoserverLayerName("buildings");
        testLayer.setVisible(true);
        testLayer.setQueryable(true);
        testLayer.setOrderIndex(1);
        testLayer.setCompanyId("company1");
        testLayer.setActive(true);
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Katman başarıyla oluşturulmalı")
    void shouldCreateLayer() {
        setSecurityContext();

        LayerRequest request = new LayerRequest();
        request.setCode("BUILDINGS");
        request.setName("Binalar");
        request.setLayerType(LayerType.VECTOR);
        request.setVisible(true);
        request.setQueryable(true);
        request.setOrderIndex(1);

        when(layerRepository.existsByCode("BUILDINGS")).thenReturn(false);
        when(layerRepository.save(any(Layer.class))).thenAnswer(inv -> {
            Layer l = inv.getArgument(0);
            l.setId(UUID.randomUUID());
            return l;
        });

        LayerResponse response = layerService.create(request);

        assertThat(response.getCode()).isEqualTo("BUILDINGS");
        assertThat(response.getCompanyId()).isEqualTo("company1");
        verify(layerRepository).save(any(Layer.class));
    }

    @Test
    @DisplayName("Mevcut kod ile katman oluşturma başarısız olmalı")
    void shouldFailCreateWithExistingCode() {
        setSecurityContext();

        LayerRequest request = new LayerRequest();
        request.setCode("BUILDINGS");

        when(layerRepository.existsByCode("BUILDINGS")).thenReturn(true);

        assertThatThrownBy(() -> layerService.create(request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    @DisplayName("Tüm katmanlar listelenebilmeli")
    void shouldGetAllLayers() {
        setSecurityContext();

        when(layerRepository.findByCompanyIdAndActiveOrderByOrderIndexAsc("company1", true))
                .thenReturn(List.of(testLayer));

        List<LayerResponse> result = layerService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("BUILDINGS");
    }

    @Test
    @DisplayName("Katman ID ile getirilmeli")
    void shouldGetLayerById() {
        UUID layerId = testLayer.getId();
        when(layerRepository.findById(layerId)).thenReturn(Optional.of(testLayer));

        LayerResponse response = layerService.getById(layerId);

        assertThat(response.getCode()).isEqualTo("BUILDINGS");
        assertThat(response.getLayerType()).isEqualTo(LayerType.VECTOR);
    }

    @Test
    @DisplayName("Katman bulunamazsa hata fırlatmalı")
    void shouldThrowWhenLayerNotFound() {
        UUID id = UUID.randomUUID();
        when(layerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> layerService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Katman başarıyla güncellenebilmeli")
    void shouldUpdateLayer() {
        UUID layerId = testLayer.getId();
        LayerRequest request = new LayerRequest();
        request.setCode("BUILDINGS");
        request.setName("Binalar v2");
        request.setLayerType(LayerType.VECTOR);
        request.setVisible(true);
        request.setQueryable(true);
        request.setOrderIndex(2);

        when(layerRepository.findById(layerId)).thenReturn(Optional.of(testLayer));
        when(layerRepository.save(any(Layer.class))).thenReturn(testLayer);

        LayerResponse response = layerService.update(layerId, request);

        assertThat(response).isNotNull();
        verify(layerRepository).save(any(Layer.class));
    }

    @Test
    @DisplayName("Katman soft delete yapılmalı")
    void shouldSoftDeleteLayer() {
        UUID layerId = testLayer.getId();
        when(layerRepository.findById(layerId)).thenReturn(Optional.of(testLayer));
        when(layerRepository.save(any(Layer.class))).thenReturn(testLayer);

        layerService.delete(layerId);

        assertThat(testLayer.getActive()).isFalse();
        verify(layerRepository).save(testLayer);
    }
}
