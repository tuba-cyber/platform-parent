package com.platform.core.security.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    // Login endpoint için özel limitler
    private static final int LOGIN_MAX_REQUESTS = 5;
    private static final int LOGIN_WINDOW_SECONDS = 60;

    // Genel API limitleri
    private static final int API_MAX_REQUESTS = 100;
    private static final int API_WINDOW_SECONDS = 60;

    private static final String RATE_LIMIT_PREFIX = "rate:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String clientIp = getClientIp(request);

        boolean isLoginEndpoint = path.equals("/api/v1/auth/login");
        int maxRequests = isLoginEndpoint ? LOGIN_MAX_REQUESTS : API_MAX_REQUESTS;
        int windowSeconds = isLoginEndpoint ? LOGIN_WINDOW_SECONDS : API_WINDOW_SECONDS;

        String key = RATE_LIMIT_PREFIX + clientIp + ":" + path;

        if (isRateLimited(key, maxRequests, windowSeconds)) {
            log.warn("Rate limit aşıldı — IP: {}, Path: {}", clientIp, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "success": false,
                    "message": "Çok fazla istek gönderildi. Lütfen bekleyin.",
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String key, int maxRequests,
            int windowSeconds) {
        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count == null) return false;

            if (count == 1) {
                // İlk istek, TTL set et
                redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
            }

            return count > maxRequests;
        } catch (Exception e) {
            // Redis hatasında engelleme
            log.error("Rate limit kontrolü yapılamadı: {}", e.getMessage());
            return false;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}