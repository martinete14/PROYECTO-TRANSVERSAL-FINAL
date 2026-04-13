package com.example.demo.config;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.RolUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CurrentUserAdvice {

    private final UsuarioRepository usuarioRepository;

    public CurrentUserAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

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

    @ModelAttribute("showUserRoleBadge")
    public boolean showUserRoleBadge(HttpSession session) {
        Object userIdObj = session.getAttribute(AuthSessionKeys.AUTH_USER_ID);
        if (!(userIdObj instanceof Long userId)) {
            return false;
        }

        return usuarioRepository.findById(userId)
            .map(usuario -> !isProfileCompleted(usuario))
            .orElse(false);
    }

    private boolean isProfileCompleted(Usuario usuario) {
        return StringUtils.hasText(usuario.getDocumento())
            || StringUtils.hasText(usuario.getTelefono())
            || usuario.getFechaNacimiento() != null
            || StringUtils.hasText(usuario.getCiudadPais())
            || StringUtils.hasText(usuario.getInstitucionAcademica())
            || StringUtils.hasText(usuario.getProgramaAcademico())
            || StringUtils.hasText(usuario.getBiografia())
            || StringUtils.hasText(usuario.getFotoPerfilUrl());
    }
}
