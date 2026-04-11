package com.platform.hr.position.service;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.position.dto.PositionRequest;
import com.platform.hr.position.dto.PositionResponse;
import com.platform.hr.position.entity.Position;
import com.platform.hr.position.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;

    @Transactional
    public ApiResponse<PositionResponse> create(PositionRequest request) {
        if (positionRepository.existsByCode(request.getCode())) {
            return ApiResponse.error("Bu kod ile zaten bir pozisyon mevcut: " + request.getCode());
        }

        Position position = Position.builder()
                .companyId(request.getCompanyId())
                .title(request.getTitle())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .departmentId(request.getDepartmentId())
                .level(request.getLevel())
                .active(true)
                .build();

        position = positionRepository.save(position);
        return ApiResponse.success(toResponse(position), "Pozisyon oluşturuldu");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<PositionResponse>> getByCompany(UUID companyId) {
        List<PositionResponse> list = positionRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Pozisyon listesi");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<PositionResponse>> getByDepartment(UUID departmentId) {
        List<PositionResponse> list = positionRepository.findByDepartmentIdAndActiveTrue(departmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Departman pozisyonları");
    }

    @Transactional(readOnly = true)
    public ApiResponse<PositionResponse> getById(UUID id) {
        return positionRepository.findById(id)
                .map(p -> ApiResponse.success(toResponse(p), "Pozisyon bulundu"))
                .orElse(ApiResponse.error("Pozisyon bulunamadı"));
    }

    @Transactional
    public ApiResponse<PositionResponse> update(UUID id, PositionRequest request) {
        return positionRepository.findById(id).map(position -> {
            if (positionRepository.existsByCodeAndIdNot(request.getCode(), id)) {
                return ApiResponse.<PositionResponse>error("Bu kod başka bir pozisyonda kullanılıyor");
            }
            position.setTitle(request.getTitle());
            position.setCode(request.getCode().toUpperCase());
            position.setDescription(request.getDescription());
            position.setDepartmentId(request.getDepartmentId());
            position.setLevel(request.getLevel());
            return ApiResponse.success(toResponse(positionRepository.save(position)), "Pozisyon güncellendi");
        }).orElse(ApiResponse.error("Pozisyon bulunamadı"));
    }

    @Transactional
    public ApiResponse<Void> delete(UUID id) {
        return positionRepository.findById(id).map(position -> {
            position.setActive(false);
            positionRepository.save(position);
            return ApiResponse.<Void>success(null, "Pozisyon pasife alındı");
        }).orElse(ApiResponse.error("Pozisyon bulunamadı"));
    }

    private PositionResponse toResponse(Position p) {
        return PositionResponse.builder()
                .id(p.getId())
                .companyId(p.getCompanyId())
                .title(p.getTitle())
                .code(p.getCode())
                .description(p.getDescription())
                .departmentId(p.getDepartmentId())
                .level(p.getLevel())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
