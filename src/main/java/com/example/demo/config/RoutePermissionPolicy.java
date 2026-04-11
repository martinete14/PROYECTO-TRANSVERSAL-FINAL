package com.example.demo.config;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.model.RolUsuario;

@Component
public class RoutePermissionPolicy {

    private static final List<RouteRule> RULES = List.of(
        new RouteRule("/web/cursos/admin", Set.of(RolUsuario.ADMIN), "ADMIN"),
        new RouteRule("/web/cursos/instructor", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/nuevo", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/crear", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/editar", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/actualizar", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/eliminar", Set.of(RolUsuario.ADMIN, RolUsuario.INSTRUCTOR), "ADMIN o INSTRUCTOR"),
        new RouteRule("/web/cursos/adquiridos", Set.of(RolUsuario.CLIENTE), "CLIENTE"),
        new RouteRule("/web/cursos/comprar", Set.of(RolUsuario.CLIENTE), "CLIENTE")
    );

    public AuthorizationDecision evaluate(String path, RolUsuario role) {
        RouteRule matchedRule = RULES.stream()
            .filter(rule -> path.startsWith(rule.pathPrefix()))
            .max(Comparator.comparingInt(rule -> rule.pathPrefix().length()))
            .orElse(null);

        if (matchedRule == null) {
            return AuthorizationDecision.allow();
        }

        if (matchedRule.allowedRoles().contains(role)) {
            return AuthorizationDecision.allow();
        }

        return AuthorizationDecision.deny(matchedRule.requiredRoleLabel());
    }

    public record RouteRule(String pathPrefix, Set<RolUsuario> allowedRoles, String requiredRoleLabel) {
    }

    public record AuthorizationDecision(boolean allowed, String requiredRoleLabel) {

        public static AuthorizationDecision allow() {
            return new AuthorizationDecision(true, null);
        }

        public static AuthorizationDecision deny(String requiredRoleLabel) {
            return new AuthorizationDecision(false, requiredRoleLabel);
        }
    }
}
