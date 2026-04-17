package com.example.demo.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.model.RolUsuario;
import com.example.demo.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private final RoutePermissionPolicy routePermissionPolicy;
    private final AuditLogService auditLogService;

    public RoleAuthorizationInterceptor(RoutePermissionPolicy routePermissionPolicy, AuditLogService auditLogService) {
        this.routePermissionPolicy = routePermissionPolicy;
        this.auditLogService = auditLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (!path.startsWith("/web/")) {
            return true;
        }

        if (path.startsWith("/web/auth/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthSessionKeys.AUTH_USER_ID) == null) {
            auditLogService.logAnonymous("ACCESS_REQUIRES_AUTH", "Ruta protegida sin sesion activa.", path, request.getRemoteAddr());
            response.sendRedirect("/web/auth/login");
            return false;
        }

        String roleValue = (String) session.getAttribute(AuthSessionKeys.AUTH_ROLE);
        if (roleValue == null || roleValue.isBlank()) {
            auditLogService.logFromSession(session, "AUTH_SESSION_INVALID", "Sesion autenticada sin rol valido.", path, request.getRemoteAddr());
            session.invalidate();
            response.sendRedirect("/web/auth/login");
            return false;
        }

        RolUsuario rol = RolUsuario.fromValue(roleValue);
        RoutePermissionPolicy.AuthorizationDecision decision = routePermissionPolicy.evaluate(path, rol);

        if (!decision.allowed()) {
            auditLogService.logFromSession(
                session,
                "ACCESS_DENIED",
                "Acceso denegado. Requiere rol: " + decision.requiredRoleLabel(),
                path,
                request.getRemoteAddr()
            );
            String required = URLEncoder.encode(decision.requiredRoleLabel(), StandardCharsets.UTF_8);
            String from = URLEncoder.encode(path, StandardCharsets.UTF_8);
            response.sendRedirect("/web/auth/denegado?required=" + required + "&from=" + from);
            return false;
        }

        return true;
    }
}
