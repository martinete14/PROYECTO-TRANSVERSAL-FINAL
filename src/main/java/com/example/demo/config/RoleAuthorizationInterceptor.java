package com.example.demo.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.model.RolUsuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private final RoutePermissionPolicy routePermissionPolicy;

    public RoleAuthorizationInterceptor(RoutePermissionPolicy routePermissionPolicy) {
        this.routePermissionPolicy = routePermissionPolicy;
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
            response.sendRedirect("/web/auth/login");
            return false;
        }

        RolUsuario rol = RolUsuario.fromValue((String) session.getAttribute("AUTH_ROLE"));
        RoutePermissionPolicy.AuthorizationDecision decision = routePermissionPolicy.evaluate(path, rol);

        if (!decision.allowed()) {
            String required = URLEncoder.encode(decision.requiredRoleLabel(), StandardCharsets.UTF_8);
            String from = URLEncoder.encode(path, StandardCharsets.UTF_8);
            response.sendRedirect("/web/auth/denegado?required=" + required + "&from=" + from);
            return false;
        }

        return true;
    }
}
