package com.platform.core.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.platform.core.security.model.UserPrincipal;
import com.platform.core.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String USER_CACHE_PREFIX = "user:";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            // 1. Blacklist kontrolü — logout edilmiş token mı?
            if (isBlacklisted(jwt)) {
                log.warn("Blacklist'teki token kullanılmaya çalışıldı");
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtService.extractUsername(jwt);

            if (username != null &&
                SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                // 2. Redis cache'den kontrol et
                UserPrincipal userPrincipal = getUserFromCache(jwt);

                if (userPrincipal == null) {
                    // Cache'de yok, token'dan oluştur
                    if (jwtService.isTokenValid(jwt, username)) {
                        userPrincipal = buildUserPrincipal(jwt, username);
                        // Cache'e kaydet
                        cacheUser(jwt, userPrincipal);
                        log.debug("Kullanıcı cache'e alındı: {}", username);
                    }
                } else {
                    log.debug("Kullanıcı cache'den alındı: {}", username);
                }

                if (userPrincipal != null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    userPrincipal.getAuthorities()
                            );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );
                    SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("JWT doğrulama hatası: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Token'ı blacklist'e ekle (logout için)
    public void blacklistToken(String token) {
        long expiration = jwtService.getExpirationTime(token);
        if (expiration > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "blacklisted",
                expiration,
                TimeUnit.MILLISECONDS
            );
            // Cache'den de sil
            redisTemplate.delete(USER_CACHE_PREFIX + token);
            log.info("Token blacklist'e eklendi");
        }
    }

    private boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(BLACKLIST_PREFIX + token)
        );
    }

    private UserPrincipal getUserFromCache(String token) {
        try {
            String cachedUsername = redisTemplate.opsForValue()
                .get(USER_CACHE_PREFIX + token);
            if (cachedUsername == null) return null;

            // Cache'de var, token'dan bilgileri al
            return buildUserPrincipal(token,
                jwtService.extractUsername(token));
        } catch (Exception e) {
            return null;
        }
    }

    private void cacheUser(String token, UserPrincipal principal) {
        try {
            long expiration = jwtService.getExpirationTime(token);
            if (expiration > 0) {
                redisTemplate.opsForValue().set(
                    USER_CACHE_PREFIX + token,
                    principal.getUsername(),
                    expiration,
                    TimeUnit.MILLISECONDS
                );
            }
        } catch (Exception e) {
            log.warn("Cache'e yazılamadı: {}", e.getMessage());
        }
    }

    private UserPrincipal buildUserPrincipal(String jwt, String username) {
        List<String> roles = jwtService.extractRoles(jwt);
        List<String> permissions = jwtService.extractPermissions(jwt);
        UUID userId = jwtService.extractUserId(jwt);
        String companyId = jwtService.extractCompanyId(jwt);

        return UserPrincipal.builder()
                .id(userId)
                .username(username)
                .password("")
                .companyId(companyId)
                .roles(roles)
                .permissions(permissions)
                .active(true)
                .build();
    }
}