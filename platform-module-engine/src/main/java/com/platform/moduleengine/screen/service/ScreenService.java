package com.platform.moduleengine.screen.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.moduleengine.screen.dto.ScreenRequest;
import com.platform.moduleengine.screen.dto.ScreenResponse;
import com.platform.moduleengine.screen.entity.Screen;
import com.platform.moduleengine.screen.repository.ScreenRepository;
import com.platform.moduleengine.section.entity.Section;
import com.platform.moduleengine.section.repository.SectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public ScreenResponse create(UUID sectionId, ScreenRequest request) {
        if (screenRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu ekran kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        Section section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new ResourceNotFoundException("Bölüm", sectionId));

        Screen screen = new Screen();
        screen.setName(request.getName());
        screen.setCode(request.getCode());
        screen.setDescription(request.getDescription());
        screen.setIcon(request.getIcon());
        screen.setOrderIndex(request.getOrderIndex());
        screen.setVisible(request.getVisible());
        screen.setTemplate(request.getTemplate());
        screen.setScreenType(request.getScreenType());
        screen.setRequiredPermission(request.getRequiredPermission());
        screen.setSection(section);

        screenRepository.save(screen);
        log.info("Ekran oluşturuldu: {}", screen.getCode());

        return toResponse(screen);
    }

    @Transactional(readOnly = true)
    public List<ScreenResponse> getBySectionId(UUID sectionId) {
        return screenRepository
            .findBySectionIdOrderByOrderIndexAsc(sectionId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScreenResponse getById(UUID id) {
        Screen screen = screenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ekran", id));
        return toResponse(screen);
    }

    @Transactional(readOnly = true)
    public ScreenResponse getByCode(String code) {
        Screen screen = screenRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Ekran", code));
        return toResponse(screen);
    }

    @Transactional
    public ScreenResponse update(UUID id, ScreenRequest request) {
        Screen screen = screenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ekran", id));

        if (!screen.getCode().equals(request.getCode()) &&
            screenRepository.existsByCode(request.getCode())) {
            throw new BaseException(
                "Bu ekran kodu zaten kullanılıyor: " + request.getCode(),
                HttpStatus.CONFLICT
            );
        }

        screen.setName(request.getName());
        screen.setCode(request.getCode());
        screen.setDescription(request.getDescription());
        screen.setIcon(request.getIcon());
        screen.setOrderIndex(request.getOrderIndex());
        screen.setVisible(request.getVisible());
        screen.setScreenType(request.getScreenType());
        screen.setRequiredPermission(request.getRequiredPermission());

        screenRepository.save(screen);
        log.info("Ekran güncellendi: {}", screen.getCode());

        return toResponse(screen);
    }

    @Transactional
    public ScreenResponse updateTemplate(UUID id, String template) {
        Screen screen = screenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ekran", id));
        screen.setTemplate(template);
        screenRepository.save(screen);
        log.info("Ekran şablonu güncellendi: {}", screen.getCode());
        return toResponse(screen);
    }

    @Transactional
    public void delete(UUID id) {
        Screen screen = screenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ekran", id));
        screen.setActive(false);
        screenRepository.save(screen);
        log.info("Ekran pasif yapıldı: {}", screen.getCode());
    }

    public ScreenResponse toResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .name(screen.getName())
                .code(screen.getCode())
                .description(screen.getDescription())
                .icon(screen.getIcon())
                .orderIndex(screen.getOrderIndex())
                .visible(screen.getVisible())
                .active(screen.getActive())
                .template(screen.getTemplate())
                .screenType(screen.getScreenType())
                .requiredPermission(screen.getRequiredPermission())
                .sectionId(screen.getSection().getId())
                .build();
    }
}