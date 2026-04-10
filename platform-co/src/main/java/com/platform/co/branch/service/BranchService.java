package com.platform.co.branch.service;

import com.platform.co.branch.dto.BranchRequest;
import com.platform.co.branch.dto.BranchResponse;
import com.platform.co.branch.entity.Branch;
import com.platform.co.branch.repository.BranchRepository;
import com.platform.core.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<BranchResponse> getByCompanyId(UUID companyId) {
        return branchRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BranchResponse getById(UUID id) {
        Branch branch = branchRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şube bulunamadı: " + id));
        return toResponse(branch);
    }

    @Transactional
    public BranchResponse create(BranchRequest request) {
        if (branchRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Bu şube kodu zaten kullanımda: " + request.getCode());
        }

        // Eğer merkez olarak işaretleniyorsa, şirketin mevcut merkezini kaldır
        if (Boolean.TRUE.equals(request.getHeadquarters())) {
            branchRepository.findByCompanyIdAndHeadquartersTrue(request.getCompanyId())
                    .ifPresent(existing -> {
                        existing.setHeadquarters(false);
                        branchRepository.save(existing);
                    });
        }

        Branch branch = new Branch();
        applyRequest(branch, request);
        Branch saved = branchRepository.save(branch);
        log.info("Şube oluşturuldu: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public BranchResponse update(UUID id, BranchRequest request) {
        Branch branch = branchRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şube bulunamadı: " + id));

        if (!branch.getCode().equals(request.getCode())
                && branchRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Bu şube kodu zaten kullanımda: " + request.getCode());
        }

        if (Boolean.TRUE.equals(request.getHeadquarters()) && !branch.getHeadquarters()) {
            branchRepository.findByCompanyIdAndHeadquartersTrue(request.getCompanyId())
                    .ifPresent(existing -> {
                        existing.setHeadquarters(false);
                        branchRepository.save(existing);
                    });
        }

        applyRequest(branch, request);
        Branch saved = branchRepository.save(branch);
        log.info("Şube güncellendi: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Branch branch = branchRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Şube bulunamadı: " + id));
        branch.setActive(false);
        branchRepository.save(branch);
        log.info("Şube silindi: {}", id);
    }

    private void applyRequest(Branch branch, BranchRequest request) {
        branch.setCompanyId(request.getCompanyId());
        branch.setName(request.getName());
        branch.setCode(request.getCode().toUpperCase());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setDistrict(request.getDistrict());
        branch.setPostalCode(request.getPostalCode());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setHeadquarters(Boolean.TRUE.equals(request.getHeadquarters()));
    }

    private BranchResponse toResponse(Branch branch) {
        BranchResponse response = new BranchResponse();
        response.setId(branch.getId());
        response.setCompanyId(branch.getCompanyId());
        response.setName(branch.getName());
        response.setCode(branch.getCode());
        response.setAddress(branch.getAddress());
        response.setCity(branch.getCity());
        response.setDistrict(branch.getDistrict());
        response.setPostalCode(branch.getPostalCode());
        response.setPhone(branch.getPhone());
        response.setEmail(branch.getEmail());
        response.setHeadquarters(branch.getHeadquarters());
        response.setActive(branch.getActive());
        response.setCreatedAt(branch.getCreatedAt());
        response.setUpdatedAt(branch.getUpdatedAt());
        return response;
    }
}
