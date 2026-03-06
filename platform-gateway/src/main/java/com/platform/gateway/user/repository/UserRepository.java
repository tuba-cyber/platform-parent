package com.platform.gateway.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.platform.gateway.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
        SELECT u FROM User u 
        LEFT JOIN FETCH u.roles r 
        LEFT JOIN FETCH r.permissions 
        WHERE u.username = :username 
        AND u.active = true
    """)
    Optional<User> findByUsernameWithRolesAndPermissions(String username);
}