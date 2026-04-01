package com.platform.moduleengine.module.service;

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

import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.moduleengine.module.dto.ModuleRequest;
import com.platform.moduleengine.module.dto.ModuleResponse;
import com.platform.moduleengine.module.entity.Module;
import com.platform.moduleengine.module.repository.ModuleRepository;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleService moduleService;

    private UserPrincipal testPrincipal;
    private Module testModule;

    @BeforeEach
    void setUp() {
        testPrincipal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .password("")
                .companyId("company1")
                .roles(List.of("ADMIN"))
                .permissions(List.of("MODULE_MANAGE"))
                .active(true)
                .build();

        testModule = new Module();
        testModule.setId(UUID.randomUUID());
        testModule.setCode("CBS");
        testModule.setName("CBS Modülü");
        testModule.setDescription("Coğrafi Bilgi Sistemi");
        testModule.setIcon("map");
        testModule.setColor("#3B82F6");
        testModule.setOrderIndex(1);
        testModule.setVisible(true);
        testModule.setCompanyId("company1");
        testModule.setActive(true);
    }

    private void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testPrincipal, null, testPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Modül başarıyla oluşturulmalı")
    void shouldCreateModule() {
        setSecurityContext();

        ModuleRequest request = new ModuleRequest();
        request.setCode("CBS");
        request.setName("CBS Modülü");
        request.setDescription("Coğrafi Bilgi Sistemi");
        request.setIcon("map");
        request.setColor("#3B82F6");
        request.setOrderIndex(1);
        request.setVisible(true);

        when(moduleRepository.existsByCode("CBS")).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenAnswer(inv -> {
            Module m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        ModuleResponse response = moduleService.create(request);

        assertThat(response.getCode()).isEqualTo("CBS");
        assertThat(response.getName()).isEqualTo("CBS Modülü");
        assertThat(response.getCompanyId()).isEqualTo("company1");
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    @DisplayName("Mevcut kod ile modül oluşturma başarısız olmalı")
    void shouldFailCreateWithExistingCode() {
        setSecurityContext();

        ModuleRequest request = new ModuleRequest();
        request.setCode("CBS");

        when(moduleRepository.existsByCode("CBS")).thenReturn(true);

        assertThatThrownBy(() -> moduleService.create(request))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("zaten kullanılıyor");
    }

    @Test
    @DisplayName("Tüm modüller listelenebilmeli")
    void shouldGetAllModules() {
        setSecurityContext();

        when(moduleRepository.findByCompanyIdAndActiveOrderByOrderIndexAsc("company1", true))
                .thenReturn(List.of(testModule));

        List<ModuleResponse> result = moduleService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("CBS");
    }

    @Test
    @DisplayName("Modül ID ile getirilmeli")
    void shouldGetModuleById() {
        UUID moduleId = testModule.getId();
        when(moduleRepository.findByIdWithSectionsAndScreens(moduleId))
                .thenReturn(Optional.of(testModule));

        ModuleResponse response = moduleService.getById(moduleId);

        assertThat(response.getCode()).isEqualTo("CBS");
    }

    @Test
    @DisplayName("Modül bulunamazsa hata fırlatmalı")
    void shouldThrowWhenModuleNotFound() {
        UUID id = UUID.randomUUID();
        when(moduleRepository.findByIdWithSectionsAndScreens(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> moduleService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Modül başarıyla güncellenebilmeli")
    void shouldUpdateModule() {
        UUID moduleId = testModule.getId();
        ModuleRequest request = new ModuleRequest();
        request.setCode("CBS");
        request.setName("CBS Modülü v2");
        request.setDescription("Güncellenmiş açıklama");
        request.setIcon("globe");
        request.setColor("#10B981");
        request.setOrderIndex(2);
        request.setVisible(true);

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        ModuleResponse response = moduleService.update(moduleId, request);

        assertThat(response).isNotNull();
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    @DisplayName("Modül soft delete yapılmalı")
    void shouldSoftDeleteModule() {
        UUID moduleId = testModule.getId();
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        moduleService.delete(moduleId);

        assertThat(testModule.getActive()).isFalse();
        verify(moduleRepository).save(testModule);
    }

    @Test
    @DisplayName("Modül aktif/pasif durumu değiştirilebilmeli")
    void shouldToggleModuleActive() {
        UUID moduleId = testModule.getId();
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        ModuleResponse response = moduleService.toggleActive(moduleId);

        assertThat(testModule.getActive()).isFalse();
        verify(moduleRepository).save(testModule);
    }
}
