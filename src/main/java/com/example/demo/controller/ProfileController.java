package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

    private static final long MAX_PROFILE_IMAGE_BYTES = 1 * 1024 * 1024;

    @Value("${app.upload.profile-images-dir:uploads/profiles}")
    private String profileImagesDir;

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
        @RequestParam(required = false) MultipartFile fotoPerfilFile,
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
        usuario.setBiografia(trimToNull(biografia));

        try {
            String oldProfileImage = usuario.getFotoPerfilUrl();
            String newProfileImage = storeProfileImageIfProvided(fotoPerfilFile);
            if (StringUtils.hasText(newProfileImage)) {
                usuario.setFotoPerfilUrl(newProfileImage);
                tryDeleteUnusedProfileImage(oldProfileImage, newProfileImage);
            }
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/web/perfil";
        }

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
            case ADMIN -> "ID de administraciÃ³n";
        };
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String storeProfileImageIfProvided(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            throw new RuntimeException("La foto de perfil debe ser una imagen valida.");
        }

        if (file.getSize() > MAX_PROFILE_IMAGE_BYTES) {
            throw new RuntimeException("La foto de perfil supera el limite de 1 MB.");
        }

        try {
            Path uploadPath = Paths.get(profileImagesDir);
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String fileName = "profile-" + UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profiles/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar la foto de perfil.", ex);
        }
    }

    private void tryDeleteUnusedProfileImage(String oldImage, String currentImage) {
        if (!StringUtils.hasText(oldImage) || Objects.equals(oldImage, currentImage) || !oldImage.startsWith("/uploads/profiles/")) {
            return;
        }

        String fileName = oldImage.substring(oldImage.lastIndexOf('/') + 1);
        if (!StringUtils.hasText(fileName)) {
            return;
        }

        Path localPath = Paths.get(profileImagesDir).resolve(fileName);
        try {
            Files.deleteIfExists(localPath);
        } catch (IOException ignored) {
            // No bloquear guardado de perfil por fallo de limpieza.
        }
    }
}
