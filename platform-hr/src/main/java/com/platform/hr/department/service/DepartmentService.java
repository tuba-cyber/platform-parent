package com.platform.hr.department.service;

import com.platform.core.common.response.ApiResponse;
import com.platform.hr.department.dto.DepartmentRequest;
import com.platform.hr.department.dto.DepartmentResponse;
import com.platform.hr.department.entity.Department;
import com.platform.hr.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public ApiResponse<DepartmentResponse> create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            return ApiResponse.error("Bu kod ile zaten bir departman mevcut: " + request.getCode());
        }

        Department department = Department.builder()
                .companyId(request.getCompanyId())
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .parentDepartmentId(request.getParentDepartmentId())
                .active(true)
                .build();

        department = departmentRepository.save(department);
        return ApiResponse.success(toResponse(department), "Departman oluşturuldu");
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<DepartmentResponse>> getByCompany(UUID companyId) {
        List<DepartmentResponse> list = departmentRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ApiResponse.success(list, "Departman listesi");
    }

    @Transactional(readOnly = true)
    public ApiResponse<DepartmentResponse> getById(UUID id) {
        return departmentRepository.findById(id)
                .map(d -> ApiResponse.success(toResponse(d), "Departman bulundu"))
                .orElse(ApiResponse.error("Departman bulunamadı"));
    }

    @Transactional
    public ApiResponse<DepartmentResponse> update(UUID id, DepartmentRequest request) {
        return departmentRepository.findById(id).map(department -> {
            if (departmentRepository.existsByCodeAndIdNot(request.getCode(), id)) {
                return ApiResponse.<DepartmentResponse>error("Bu kod başka bir departmanda kullanılıyor");
            }
            department.setName(request.getName());
            department.setCode(request.getCode().toUpperCase());
            department.setDescription(request.getDescription());
            department.setParentDepartmentId(request.getParentDepartmentId());
            return ApiResponse.success(toResponse(departmentRepository.save(department)), "Departman güncellendi");
        }).orElse(ApiResponse.error("Departman bulunamadı"));
    }

    @Transactional
    public ApiResponse<Void> delete(UUID id) {
        return departmentRepository.findById(id).map(department -> {
            department.setActive(false);
            departmentRepository.save(department);
            return ApiResponse.<Void>success(null, "Departman pasife alındı");
        }).orElse(ApiResponse.error("Departman bulunamadı"));
    }

    private DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .companyId(d.getCompanyId())
                .name(d.getName())
                .code(d.getCode())
                .description(d.getDescription())
                .parentDepartmentId(d.getParentDepartmentId())
                .active(d.isActive())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
