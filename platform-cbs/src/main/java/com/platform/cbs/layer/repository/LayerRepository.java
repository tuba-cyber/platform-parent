package com.platform.cbs.layer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.cbs.layer.entity.Layer;
import com.platform.cbs.layer.entity.LayerType;

@Repository
public interface LayerRepository extends JpaRepository<Layer, UUID> {

    List<Layer> findByCompanyIdAndActiveOrderByOrderIndexAsc(
        String companyId, Boolean active
    );

    List<Layer> findByCompanyIdAndLayerTypeAndActiveOrderByOrderIndexAsc(
        String companyId, LayerType layerType, Boolean active
    );

    Optional<Layer> findByCode(String code);

    boolean existsByCode(String code);
}