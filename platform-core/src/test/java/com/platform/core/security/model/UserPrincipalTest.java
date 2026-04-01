package com.platform.core.security.model;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class UserPrincipalTest {

    @Test
    @DisplayName("UserPrincipal oluşturulabilmeli")
    void shouldCreateUserPrincipal() {
        UUID id = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.builder()
                .id(id)
                .username("testuser")
                .password("encoded-pass")
                .companyId("company1")
                .roles(List.of("ADMIN", "USER"))
                .permissions(List.of("USER_READ", "USER_WRITE"))
                .active(true)
                .build();

        assertThat(principal.getId()).isEqualTo(id);
        assertThat(principal.getUsername()).isEqualTo("testuser");
        assertThat(principal.getCompanyId()).isEqualTo("company1");
        assertThat(principal.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Authorities roller ve yetkileri içermeli")
    void shouldCombineRolesAndPermissions() {
        UserPrincipal principal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("pass")
                .companyId("c1")
                .roles(List.of("ADMIN", "USER"))
                .permissions(List.of("USER_READ", "MODULE_WRITE"))
                .active(true)
                .build();

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        List<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorityNames).contains("ROLE_ADMIN", "ROLE_USER", "USER_READ", "MODULE_WRITE");
        assertThat(authorityNames).hasSize(4);
    }

    @Test
    @DisplayName("Aktif olmayan kullanıcı hesabı kilitli olmalı")
    void shouldLockInactiveAccount() {
        UserPrincipal principal = UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .password("pass")
                .companyId("c1")
                .roles(List.of("USER"))
                .permissions(List.of())
                .active(false)
                .build();

        assertThat(principal.isAccountNonLocked()).isFalse();
        assertThat(principal.isEnabled()).isFalse();
        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
    }
}
