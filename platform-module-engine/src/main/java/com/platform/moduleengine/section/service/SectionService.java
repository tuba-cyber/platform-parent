package com.platform.moduleengine.section.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.moduleengine.module.entity.Module;
import com.platform.moduleengine.module.repository.ModuleRepository;
import com.platform.moduleengine.screen.dto.ScreenResponse;
import com.platform.moduleengine.section.dto.SectionRequest;
import com.platform.moduleengine.section.dto.SectionResponse;
import com.platform.moduleengine.section.entity.Section;
import com.platform.moduleengine.section.repository.SectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final ModuleRepository moduleRepository;

    @Transactional
    public SectionResponse create(UUID moduleId, SectionRequest request) {
        Module module = moduleRepository.findById(moduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Modül", moduleId));

        Section section = new Section();
        section.setName(request.getName());
        section.setDescription(request.getDescription());
        section.setIcon(request.getIcon());
        section.setOrderIndex(request.getOrderIndex());
        section.setVisible(request.getVisible());
        section.setModule(module);

        sectionRepository.save(section);
        log.info("Bölüm oluşturuldu: {} → {}", module.getCode(), section.getName());

        return toResponse(section, false);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getByModuleId(UUID moduleId) {
        return sectionRepository
            .findByModuleIdAndActiveOrderByOrderIndexAsc(moduleId, true)
            .stream()
            .map(s -> toResponse(s, false))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SectionResponse getById(UUID id) {
        Section section = sectionRepository.findByIdWithScreens(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bölüm", id));
        return toResponse(section, true);
    }

    @Transactional
    public SectionResponse update(UUID id, SectionRequest request) {
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bölüm", id));

        section.setName(request.getName());
        section.setDescription(request.getDescription());
        section.setIcon(request.getIcon());
        section.setOrderIndex(request.getOrderIndex());
        section.setVisible(request.getVisible());

        sectionRepository.save(section);
        log.info("Bölüm güncellendi: {}", section.getName());

        return toResponse(section, false);
    }

    @Transactional
    public void delete(UUID id) {
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bölüm", id));
        section.setActive(false);
        sectionRepository.save(section);
        log.info("Bölüm pasif yapıldı: {}", section.getName());
    }

    public SectionResponse toResponse(Section section, boolean includeScreens) {
        return SectionResponse.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .icon(section.getIcon())
                .orderIndex(section.getOrderIndex())
                .visible(section.getVisible())
                .active(section.getActive())
                .moduleId(section.getModule().getId())
                .screens(includeScreens && section.getScreens() != null
                    ? section.getScreens().stream()
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
                            .sectionId(section.getId())
                            .build())
                        .collect(Collectors.toList())
                    : null)
                .build();
    }
}