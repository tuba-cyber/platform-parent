package com.platform.core.security.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.platform.core.security.model.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenUserDetailsService implements UserDetailsService {

    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return UserPrincipal.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("")
                .companyId("")
                .roles(new ArrayList<>())
                .permissions(new ArrayList<>())
                .active(true)
                .build();
    }
}