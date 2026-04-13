package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/web/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (session.getAttribute(AuthSessionKeys.AUTH_USER_ID) != null) {
            return "redirect:/web/cursos";
        }

        model.addAttribute("defaultEmail", "alumno.demo@miniacademia.local");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
        @RequestParam String email,
        @RequestParam String password,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes
    ) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email y contraseña son obligatorios.");
            return "redirect:/web/auth/login";
        }

        String normalizedEmail = email.trim();
        String rawPassword = password.trim();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElse(null);

        if (usuario == null || !isPasswordValid(rawPassword, usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Credenciales inválidas.");
            return "redirect:/web/auth/login";
        }

        upgradeLegacyPasswordIfNeeded(usuario, rawPassword);

        RolUsuario rol = RolUsuario.fromValue(usuario.getRol());

        HttpSession previousSession = request.getSession(false);
        if (previousSession != null) {
            previousSession.invalidate();
        }

        HttpSession session = request.getSession(true);

        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, usuario.getId());
        session.setAttribute(AuthSessionKeys.AUTH_NAME, usuario.getNombre());
        session.setAttribute(AuthSessionKeys.AUTH_ROLE, rol.name());

        redirectAttributes.addFlashAttribute("successMessage", "Bienvenido, " + usuario.getNombre() + ".");
        return switch (rol) {
            case ADMIN -> "redirect:/web/cursos/admin";
            case INSTRUCTOR -> "redirect:/web/cursos/instructor";
            case CLIENTE -> "redirect:/web/cursos";
        };
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/web/auth/login";
    }

    private boolean isPasswordValid(String rawPassword, String storedPassword) {
        if (!StringUtils.hasText(storedPassword)) {
            return false;
        }

        String normalizedStoredPassword = storedPassword.trim();
        if (looksLikeBCryptHash(normalizedStoredPassword)) {
            return passwordEncoder.matches(rawPassword, normalizedStoredPassword);
        }

        return rawPassword.equals(normalizedStoredPassword);
    }

    private void upgradeLegacyPasswordIfNeeded(Usuario usuario, String rawPassword) {
        String storedPassword = usuario.getPassword();
        if (!StringUtils.hasText(storedPassword) || looksLikeBCryptHash(storedPassword.trim())) {
            return;
        }

        usuario.setPassword(passwordEncoder.encode(rawPassword));
        usuarioRepository.save(usuario);
    }

    private boolean looksLikeBCryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    @GetMapping("/denegado")
    public String accesoDenegado(
        @RequestParam(required = false) String required,
        @RequestParam(required = false) String from,
        Model model
    ) {
        String rolRequerido = StringUtils.hasText(required) ? required : "ADMIN";
        String rutaSolicitada = StringUtils.hasText(from) ? from : "/web/cursos";

        model.addAttribute("requiredRoleMessage", rolRequerido);
        model.addAttribute("requestedPath", rutaSolicitada);
        return "acceso-denegado";
    }
}
