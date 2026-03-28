package com.platform.core.audit.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.platform.core.audit.annotation.Auditable;
import com.platform.core.audit.entity.AuditStatus;
import com.platform.core.audit.service.AuditLogService;
import com.platform.core.security.model.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(com.platform.core.audit.annotation.Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Kullanıcı bilgilerini al
        String username = "anonymous";
        String companyId = null;

        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                username = principal.getUsername();
                companyId = principal.getCompanyId();
            }
        } catch (Exception ignored) {}

        // HTTP istek bilgilerini al
        String httpMethod = "";
        String requestUrl = "";
        String clientIp = "";

        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                httpMethod = request.getMethod();
                requestUrl = request.getRequestURI();
                clientIp = getClientIp(request);
            }
        } catch (Exception ignored) {}

        // Annotation bilgilerini al
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        String action = auditable.action().isEmpty()
            ? method.getName() : auditable.action();
        String entityType = auditable.entityType();

        // Metodu çalıştır
        Object result = null;
        AuditStatus status = AuditStatus.SUCCESS;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            status = AuditStatus.FAILURE;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;

            auditLogService.log(
                username, companyId, action, entityType,
                null, httpMethod, requestUrl, clientIp,
                status, errorMessage, durationMs
            );
        }

        return result;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}