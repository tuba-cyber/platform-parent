package com.platform.core.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
            final String username = jwtService.extractUsername(jwt);

            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                List<String> roles = jwtService.extractRoles(jwt);
                List<String> permissions = jwtService.extractPermissions(jwt);
                UUID userId = jwtService.extractUserId(jwt);
                String companyId = jwtService.extractCompanyId(jwt);

                UserPrincipal userPrincipal = UserPrincipal.builder()
                        .id(userId)
                        .username(username)
                        .password("")
                        .companyId(companyId)
                        .roles(roles)
                        .permissions(permissions)
                        .active(true)
                        .build();

                if (jwtService.isTokenValid(jwt, username)) {
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
}