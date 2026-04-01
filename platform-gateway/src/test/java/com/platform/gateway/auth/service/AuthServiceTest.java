package com.platform.gateway.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.platform.core.common.exception.BaseException;
import com.platform.core.common.exception.ResourceNotFoundException;
import com.platform.core.security.filter.JwtAuthFilter;
import com.platform.core.security.model.UserPrincipal;
import com.platform.core.security.service.JwtService;
import com.platform.gateway.auth.dto.LoginRequest;
import com.platform.gateway.auth.dto.LoginResponse;
import com.platform.gateway.auth.dto.RefreshTokenRequest;
import com.platform.gateway.auth.dto.RegisterRequest;
import com.platform.gateway.company.entity.Company;
import com.platform.gateway.company.repository.CompanyRepository;
import com.platform.gateway.user.entity.Permission;
import com.platform.gateway.user.entity.Role;
import com.platform.gateway.user.entity.User;
import com.platform.gateway.user.repository.RoleRepository;
import com.platform.gateway.user.repository.UserRepository;
import com.platform.gateway.user.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsServiceImpl userDetailsService;
    @Mock private JwtAuthFilter jwtAuthFilter;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Company testCompany;
    private Role testRole;
    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(UUID.randomUUID());
        testCompany.setName("Test Company");
        testCompany.setCode("TEST");

        Permission permission = new Permission();
        permission.setName("USER_READ");
        permission.setModule("users");

        testRole = new Role();
        testRole.setName("USER");
        testRole.setPermissions(Set.of(permission));

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCompany(testCompany);
        testUser.setRoles(new HashSet<>(Set.of(testRole)));

        testPrincipal = UserPrincipal.builder()
                .id(testUser.getId())
                .username("testuser")
                .password("encodedPassword")
                .companyId(testCompany.getId().toString())
                .roles(List.of("USER"))
                .permissions(List.of("USER_READ"))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Başarılı giriş yapılmalı")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testPrincipal);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString(), any(UUID.class), anyString(), anyList(), anyList()))
                .thenReturn("access-token");
        when(jwtService.generateRefreshToken("testuser")).thenReturn("refresh-token");

        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getFullName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("Yanlış şifre ile giriş başarısız olmalı")
    void shouldFailLoginWithWrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Kullanıcı bulunamazsa hata fırlatmalı")
    void shouldFailLoginWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("nonexistent")).thenReturn(testPrincipal);
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Başarılı kayıt yapılmalı")
    void shouldRegisterSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFirstName("New");
        request.setLastName("User");
        request.setCompanyCode("TEST");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(companyRepository.findByCode("TEST")).thenReturn(Optional.of(testCompany));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(testRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            u.setCompany(testCompany);
            return u;
        });

        // For the internal login() call
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("newuser")).thenReturn(
                UserPrincipal.builder()
                        .id(UUID.randomUUID())
                        .username("newuser")
                        .password("encodedPassword")
                        .companyId(testCompany.getId().toString())
                        .roles(List.of("USER"))
                        .permissions(List.of("USER_READ"))
                        .active(true)
                        .build()
        );
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(anyString(), any(UUID.class), anyString(), anyList(), anyList()))
                .thenReturn("new-access-token");
        when(jwtService.generateRefreshToken("newuser")).thenReturn("new-refresh-token");

        LoginResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Mevcut username ile kayıt başarısız olmalı")
    void shouldFailRegisterWithExistingUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("new@example.com");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("kullanıcı adı zaten kullanılıyor");
    }

    @Test
    @DisplayName("Mevcut email ile kayıt başarısız olmalı")
    void shouldFailRegisterWithExistingEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("existing@example.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("email zaten kullanılıyor");
    }

    @Test
    @DisplayName("Refresh token başarılı olmalı")
    void shouldRefreshTokenSuccessfully() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        when(jwtService.extractUsername("valid-refresh-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testPrincipal);
        when(jwtService.isTokenValid("valid-refresh-token", "testuser")).thenReturn(true);
        when(jwtService.generateToken(anyString(), any(UUID.class), anyString(), anyList(), anyList()))
                .thenReturn("new-access-token");

        LoginResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("valid-refresh-token");
    }

    @Test
    @DisplayName("Geçersiz refresh token hata fırlatmalı")
    void shouldFailRefreshWithInvalidToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-refresh-token");

        when(jwtService.extractUsername("invalid-refresh-token")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testPrincipal);
        when(jwtService.isTokenValid("invalid-refresh-token", "testuser")).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining("Geçersiz refresh token");
    }

    @Test
    @DisplayName("Logout başarılı olmalı")
    void shouldLogoutSuccessfully() {
        authService.logout("some-token");
        verify(jwtAuthFilter, times(1)).blacklistToken("some-token");
    }
}
