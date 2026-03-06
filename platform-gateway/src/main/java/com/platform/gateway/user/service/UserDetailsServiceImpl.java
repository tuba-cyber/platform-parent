package com.platform.gateway.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.security.model.UserPrincipal;
import com.platform.gateway.user.entity.User;
import com.platform.gateway.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {

        User user = userRepository
            .findByUsernameWithRolesAndPermissions(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Kullanıcı bulunamadı: " + username
            ));

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .companyId(user.getCompany().getId().toString())
                .roles(roles)
                .permissions(permissions)
                .active(user.getActive())
                .build();
    }
}