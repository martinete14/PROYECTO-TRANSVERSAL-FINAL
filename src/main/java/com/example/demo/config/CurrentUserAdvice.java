package com.example.demo.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.RolUsuario;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(AuthSessionKeys.AUTH_USER_ID) != null;
    }

    @ModelAttribute("currentUserName")
    public String currentUserName(HttpSession session) {
        Object value = session.getAttribute(AuthSessionKeys.AUTH_NAME);
        return value == null ? "" : value.toString();
    }

    @ModelAttribute("currentRole")
    public String currentRole(HttpSession session) {
        Object value = session.getAttribute(AuthSessionKeys.AUTH_ROLE);
        return value == null ? "CLIENTE" : value.toString();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpSession session) {
        return RolUsuario.fromValue((String) session.getAttribute(AuthSessionKeys.AUTH_ROLE)) == RolUsuario.ADMIN;
    }

    @ModelAttribute("isInstructor")
    public boolean isInstructor(HttpSession session) {
        RolUsuario rol = RolUsuario.fromValue((String) session.getAttribute(AuthSessionKeys.AUTH_ROLE));
        return rol == RolUsuario.INSTRUCTOR;
    }

    @ModelAttribute("isCliente")
    public boolean isCliente(HttpSession session) {
        return RolUsuario.fromValue((String) session.getAttribute(AuthSessionKeys.AUTH_ROLE)) == RolUsuario.CLIENTE;
    }

    @ModelAttribute("canManageCourses")
    public boolean canManageCourses(HttpSession session) {
        RolUsuario rol = RolUsuario.fromValue((String) session.getAttribute(AuthSessionKeys.AUTH_ROLE));
        return rol == RolUsuario.ADMIN || rol == RolUsuario.INSTRUCTOR;
    }
}
