package com.platform.moduleengine.navigation.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.moduleengine.navigation.entity.NavigationItem;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, UUID> {

    // Sadece ana menü öğelerini getir (parent'ı olmayanlar)
    @Query("""
        SELECT n FROM NavigationItem n
        LEFT JOIN FETCH n.children
        WHERE n.companyId = :companyId
        AND n.parent IS NULL
        AND n.active = true
        ORDER BY n.orderIndex ASC
    """)
    List<NavigationItem> findRootItemsByCompanyId(@Param("companyId") String companyId);

    List<NavigationItem> findByCompanyIdAndActiveOrderByOrderIndexAsc(
        String companyId, Boolean active
    );
}