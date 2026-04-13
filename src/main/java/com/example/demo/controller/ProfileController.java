package com.example.demo.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.AuthSessionKeys;
import com.example.demo.model.RolUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.AuditLogService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/web/perfil")
public class ProfileController {

    private final UsuarioRepository usuarioRepository;
    private final AuditLogService auditLogService;

    public ProfileController(UsuarioRepository usuarioRepository, AuditLogService auditLogService) {
        this.usuarioRepository = usuarioRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuario = getCurrentUser(session);
        ensureAcademicIdentifier(usuario);
        usuarioRepository.save(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("identificadorPerfil", resolveProfileIdentifier(usuario));
        model.addAttribute("identificadorEtiqueta", resolveIdentifierLabel(RolUsuario.fromValue(usuario.getRol())));
        return "perfil";
    }

    @PostMapping("/guardar")
    public String guardarPerfil(
        @RequestParam String nombre,
        @RequestParam(required = false) String documento,
        @RequestParam(required = false) String telefono,
        @RequestParam(required = false) String fechaNacimiento,
        @RequestParam(required = false) String ciudadPais,
        @RequestParam(required = false) String institucionAcademica,
        @RequestParam(required = false) String programaAcademico,
        @RequestParam(required = false) String nivelAcademico,
        @RequestParam(required = false) String fotoPerfilUrl,
        @RequestParam(required = false) String biografia,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = getCurrentUser(session);

        if (!StringUtils.hasText(nombre) || nombre.trim().length() < 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "El nombre debe tener al menos 3 caracteres.");
            return "redirect:/web/perfil";
        }

        usuario.setNombre(nombre.trim());
        usuario.setDocumento(trimToNull(documento));
        usuario.setTelefono(trimToNull(telefono));
        usuario.setCiudadPais(trimToNull(ciudadPais));
        usuario.setInstitucionAcademica(trimToNull(institucionAcademica));
        usuario.setProgramaAcademico(trimToNull(programaAcademico));
        usuario.setNivelAcademico(trimToNull(nivelAcademico));
        usuario.setFotoPerfilUrl(trimToNull(fotoPerfilUrl));
        usuario.setBiografia(trimToNull(biografia));

        if (StringUtils.hasText(fechaNacimiento)) {
            try {
                usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            } catch (RuntimeException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", "La fecha de nacimiento no tiene un formato valido.");
                return "redirect:/web/perfil";
            }
        } else {
            usuario.setFechaNacimiento(null);
        }

        ensureAcademicIdentifier(usuario);
        usuarioRepository.save(usuario);

        session.setAttribute(AuthSessionKeys.AUTH_NAME, usuario.getNombre());

        auditLogService.logFromSession(
            session,
            "PROFILE_UPDATED",
            "Actualizacion de datos basicos del perfil.",
            "/web/perfil/guardar",
            null
        );

        redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente.");
        return "redirect:/web/perfil";
    }

    private Usuario getCurrentUser(HttpSession session) {
        Object authUserId = session.getAttribute(AuthSessionKeys.AUTH_USER_ID);
        if (!(authUserId instanceof Long userId)) {
            throw new RuntimeException("Sesion invalida. Inicia sesion nuevamente.");
        }

        return usuarioRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado para la sesion actual."));
    }

    private void ensureAcademicIdentifier(Usuario usuario) {
        RolUsuario rol = RolUsuario.fromValue(usuario.getRol());
        switch (rol) {
            case CLIENTE -> {
                if (!StringUtils.hasText(usuario.getStudentId())) {
                    usuario.setStudentId(generateRoleIdentifier("EST", usuario.getId()));
                }
            }
            case INSTRUCTOR -> {
                if (!StringUtils.hasText(usuario.getTeacherId())) {
                    usuario.setTeacherId(generateRoleIdentifier("PROF", usuario.getId()));
                }
            }
            case ADMIN -> {
                if (!StringUtils.hasText(usuario.getAdminId())) {
                    usuario.setAdminId(generateRoleIdentifier("ADM", usuario.getId()));
                }
            }
        }
    }

    private String generateRoleIdentifier(String prefix, Long userId) {
        String seed = userId == null ? UUID.randomUUID().toString().replace("-", "").substring(0, 6) : String.format("%06d", userId);
        return "MA-" + prefix + "-" + seed;
    }

    private String resolveProfileIdentifier(Usuario usuario) {
        RolUsuario rol = RolUsuario.fromValue(usuario.getRol());
        return switch (rol) {
            case CLIENTE -> usuario.getStudentId();
            case INSTRUCTOR -> usuario.getTeacherId();
            case ADMIN -> usuario.getAdminId();
        };
    }

    private String resolveIdentifierLabel(RolUsuario rol) {
        return switch (rol) {
            case CLIENTE -> "ID de estudiante";
            case INSTRUCTOR -> "ID de profesor";
            case ADMIN -> "ID de administracion";
        };
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
