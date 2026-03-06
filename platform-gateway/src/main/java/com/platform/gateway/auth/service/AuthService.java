package com.platform.gateway.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.model.UserPrincipal;
import com.platform.core.security.service.JwtService;
import com.platform.gateway.auth.dto.LoginRequest;
import com.platform.gateway.auth.dto.LoginResponse;
import com.platform.gateway.auth.dto.RefreshTokenRequest;
import com.platform.gateway.auth.dto.RegisterRequest;
import com.platform.gateway.company.entity.Company;
import com.platform.gateway.company.repository.CompanyRepository;
import com.platform.gateway.user.entity.Role;
import com.platform.gateway.user.entity.User;
import com.platform.gateway.user.repository.RoleRepository;
import com.platform.gateway.user.repository.UserRepository;
import com.platform.gateway.user.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        UserPrincipal userPrincipal = (UserPrincipal) 
            userDetailsService.loadUserByUsername(request.getUsername());

        User user = userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Kullanıcı", request.getUsername()
            ));

        String accessToken = jwtService.generateToken(
            userPrincipal.getUsername(),
            userPrincipal.getId(),
            userPrincipal.getCompanyId(),
            userPrincipal.getRoles(),
            userPrincipal.getPermissions()
        );

        String refreshToken = jwtService.generateRefreshToken(
            userPrincipal.getUsername()
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .companyId(user.getCompany().getId().toString())
                .companyName(user.getCompany().getName())
                .roles(userPrincipal.getRoles())
                .permissions(userPrincipal.getPermissions())
                .build();
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(
                "Bu kullanıcı adı zaten kullanılıyor",
                HttpStatus.CONFLICT
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(
                "Bu email zaten kullanılıyor",
                HttpStatus.CONFLICT
            );
        }

        Company company = companyRepository
            .findByCode(request.getCompanyCode())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Şirket", request.getCompanyCode()
            ));

        Role defaultRole = roleRepository
            .findByName("USER")
            .orElseThrow(() -> new ResourceNotFoundException(
                "Varsayılan rol bulunamadı"
            ));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCompany(company);
        user.getRoles().add(defaultRole);

        userRepository.save(user);
        log.info("Yeni kullanıcı kaydedildi: {}", user.getUsername());

        return login(new LoginRequest() {{
            setUsername(request.getUsername());
            setPassword(request.getPassword());
        }});
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefreshToken());

        UserPrincipal userPrincipal = (UserPrincipal) 
            userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(request.getRefreshToken(), username)) {
            throw new BaseException(
                "Geçersiz refresh token",
                HttpStatus.UNAUTHORIZED
            );
        }

        String accessToken = jwtService.generateToken(
            userPrincipal.getUsername(),
            userPrincipal.getId(),
            userPrincipal.getCompanyId(),
            userPrincipal.getRoles(),
            userPrincipal.getPermissions()
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .username(userPrincipal.getUsername())
                .roles(userPrincipal.getRoles())
                .permissions(userPrincipal.getPermissions())
                .build();
    }
}