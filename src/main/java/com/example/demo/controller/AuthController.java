package com.example.demo.controller;

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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/web/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email y contraseña son obligatorios.");
            return "redirect:/web/auth/login";
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim())
            .orElse(null);

        if (usuario == null || !password.trim().equals(usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Credenciales inválidas.");
            return "redirect:/web/auth/login";
        }

        RolUsuario rol = RolUsuario.fromValue(usuario.getRol());

        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, usuario.getId());
        session.setAttribute("AUTH_NAME", usuario.getNombre());
        session.setAttribute("AUTH_ROLE", rol.name());

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
}
