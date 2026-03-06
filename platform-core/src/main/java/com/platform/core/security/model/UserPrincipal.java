package com.platform.core.security.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Getter;

// Kullanıcının kimlik bilgilerini, rollerini ve yetkilerini taşır. 
//Spring Security bu nesneyi SecurityContext'te tutar, 
//yani uygulama içinde her yerden erişilebilir.

@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String username;
    private String password;
    private String companyId;
    private List<String> roles;
    private List<String> permissions;
    private boolean active;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        List<GrantedAuthority> permissionAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return Stream.concat(
                roleAuthorities.stream(), 
                permissionAuthorities.stream()
        ).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }
}