package com.platform.moduleengine.module.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.moduleengine.module.dto.ModuleRequest;
import com.platform.moduleengine.module.dto.ModuleResponse;
import com.platform.moduleengine.module.entity.Module;
import com.platform.moduleengine.module.repository.ModuleRepository;
import com.platform.moduleengine.screen.dto.ScreenResponse;
import com.platform.moduleengine.section.dto.SectionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;

    @Transactional
    public ModuleResponse create(ModuleRequest request) {
        if (moduleRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu modül kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        String companyId = getCurrentCompanyId();

        Module module = new Module();
        module.setCode(request.getCode());
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setIcon(request.getIcon());
        module.setColor(request.getColor());
        module.setOrderIndex(request.getOrderIndex());
        module.setVisible(request.getVisible());
        module.setCompanyId(companyId);

        moduleRepository.save(module);
        log.info("Modül oluşturuldu: {}", module.getCode());

        return toResponse(module, false);
    }

    @Transactional(readOnly = true)
    public List<ModuleResponse> getAll() {
        String companyId = getCurrentCompanyId();
        return moduleRepository
            .findByCompanyIdAndActiveOrderByOrderIndexAsc(companyId, true)
            .stream()
            .map(m -> toResponse(m, false))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModuleResponse getById(UUID id) {
        Module module = moduleRepository
            .findByIdWithSectionsAndScreens(id)
            .orElseThrow(() -> new ResourceNotFoundException("Modül", id));
        return toResponse(module, true);
    }

    @Transactional
    public ModuleResponse update(UUID id, ModuleRequest request) {
        Module module = moduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Modül", id));

        if (!module.getCode().equals(request.getCode()) &&
            moduleRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu modül kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        module.setCode(request.getCode());
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setIcon(request.getIcon());
        module.setColor(request.getColor());
        module.setOrderIndex(request.getOrderIndex());
        module.setVisible(request.getVisible());

        moduleRepository.save(module);
        log.info("Modül güncellendi: {}", module.getCode());

        return toResponse(module, false);
    }

    @Transactional
    public void delete(UUID id) {
        Module module = moduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Modül", id));
        module.setActive(false);
        moduleRepository.save(module);
        log.info("Modül pasif yapıldı: {}", module.getCode());
    }

    @Transactional
    public ModuleResponse toggleActive(UUID id) {
        Module module = moduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Modül", id));
        module.setActive(!module.getActive());
        moduleRepository.save(module);
        log.info("Modül durumu değiştirildi: {} → {}",
            module.getCode(), module.getActive());
        return toResponse(module, false);
    }

    private String getCurrentCompanyId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    public ModuleResponse toResponse(Module module, boolean includeSections) {
        return ModuleResponse.builder()
                .id(module.getId())
                .code(module.getCode())
                .name(module.getName())
                .description(module.getDescription())
                .icon(module.getIcon())
                .color(module.getColor())
                .orderIndex(module.getOrderIndex())
                .visible(module.getVisible())
                .active(module.getActive())
                .companyId(module.getCompanyId())
                .sections(includeSections && module.getSections() != null
                    ? module.getSections().stream()
                        .map(s -> SectionResponse.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .description(s.getDescription())
                            .icon(s.getIcon())
                            .orderIndex(s.getOrderIndex())
                            .visible(s.getVisible())
                            .active(s.getActive())
                            .moduleId(module.getId())
                            .screens(s.getScreens() != null
                                ? s.getScreens().stream()
                                    .map(sc -> ScreenResponse.builder()
                                        .id(sc.getId())
                                        .name(sc.getName())
                                        .code(sc.getCode())
                                        .description(sc.getDescription())
                                        .icon(sc.getIcon())
                                        .orderIndex(sc.getOrderIndex())
                                        .visible(sc.getVisible())
                                        .active(sc.getActive())
                                        .screenType(sc.getScreenType())
                                        .requiredPermission(sc.getRequiredPermission())
                                        .sectionId(s.getId())
                                        .build())
                                    .collect(Collectors.toList())
                                : null)
                            .build())
                        .collect(Collectors.toList())
                    : null)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();
    }
}