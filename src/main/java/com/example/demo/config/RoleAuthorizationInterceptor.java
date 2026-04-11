package com.example.demo.config;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.model.RolUsuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

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

        if (path.startsWith("/web/cursos/admin") && rol != RolUsuario.ADMIN) {
            response.sendRedirect("/web/auth/denegado");
            return false;
        }

        Set<String> rutasGestion = Set.of(
            "/web/cursos/nuevo",
            "/web/cursos/crear",
            "/web/cursos/editar",
            "/web/cursos/actualizar",
            "/web/cursos/eliminar"
        );

        boolean requiereGestion = rutasGestion.stream().anyMatch(path::startsWith);
        if (requiereGestion && rol != RolUsuario.ADMIN && rol != RolUsuario.INSTRUCTOR) {
            response.sendRedirect("/web/auth/denegado");
            return false;
        }

        if (path.startsWith("/web/cursos/instructor") && rol != RolUsuario.ADMIN && rol != RolUsuario.INSTRUCTOR) {
            response.sendRedirect("/web/auth/denegado");
            return false;
        }

        return true;
    }
}
