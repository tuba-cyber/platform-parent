package com.platform.moduleengine.screen.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.platform.moduleengine.screen.entity.Screen;
import com.platform.moduleengine.screen.entity.ScreenType;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, UUID> {

    List<Screen> findBySectionIdOrderByOrderIndexAsc(UUID sectionId);

    Optional<Screen> findByCode(String code);

    boolean existsByCode(String code);

    List<Screen> findByScreenType(ScreenType screenType);
}