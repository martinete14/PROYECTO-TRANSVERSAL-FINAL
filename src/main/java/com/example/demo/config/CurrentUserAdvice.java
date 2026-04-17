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
        return loadCurrentUser(session)
            .map(usuario -> !isProfileCompleted(usuario))
            .orElse(false);
    }

    @ModelAttribute("currentUserPhotoUrl")
    public String currentUserPhotoUrl(HttpSession session) {
        return loadCurrentUser(session)
            .map(Usuario::getFotoPerfilUrl)
            .filter(StringUtils::hasText)
            .orElse(null);
    }

    @ModelAttribute("currentUserInitials")
    public String currentUserInitials(HttpSession session) {
        String name = currentUserName(session);
        if (!StringUtils.hasText(name)) {
            return "U";
        }

        String[] parts = name.trim().split("\\s+");
        String first = parts[0].substring(0, 1).toUpperCase();
        String second = parts.length > 1 ? parts[parts.length - 1].substring(0, 1).toUpperCase() : "";
        return (first + second);
    }

    @ModelAttribute("myCoursesUrl")
    public String myCoursesUrl(HttpSession session) {
        RolUsuario rol = RolUsuario.fromValue((String) session.getAttribute(AuthSessionKeys.AUTH_ROLE));
        return switch (rol) {
            case ADMIN -> "/web/cursos/admin";
            case INSTRUCTOR -> "/web/cursos/instructor";
            case CLIENTE -> "/web/cursos/adquiridos";
        };
    }

    @ModelAttribute("myCoursesLabel")
    public String myCoursesLabel() {
        return "Mis cursos";
    }

    private java.util.Optional<Usuario> loadCurrentUser(HttpSession session) {
        Object userIdObj = session.getAttribute(AuthSessionKeys.AUTH_USER_ID);
        if (!(userIdObj instanceof Long userId)) {
            return java.util.Optional.empty();
        }
        return usuarioRepository.findById(userId);
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
