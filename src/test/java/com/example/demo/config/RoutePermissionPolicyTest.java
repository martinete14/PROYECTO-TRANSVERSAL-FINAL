package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.model.RolUsuario;

class RoutePermissionPolicyTest {

    private RoutePermissionPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new RoutePermissionPolicy();
    }

    @Test
    void adminRoute_requiresAdmin() {
        var decision = policy.evaluate("/web/cursos/admin", RolUsuario.INSTRUCTOR);

        assertFalse(decision.allowed());
        assertTrue(decision.requiredRoleLabel().contains("ADMIN"));
    }

    @Test
    void adminRoute_allowsAdmin() {
        var decision = policy.evaluate("/web/cursos/admin", RolUsuario.ADMIN);

        assertTrue(decision.allowed());
    }

    @Test
    void instructorPanel_allowsInstructorAndAdmin() {
        assertTrue(policy.evaluate("/web/cursos/instructor", RolUsuario.INSTRUCTOR).allowed());
        assertTrue(policy.evaluate("/web/cursos/instructor", RolUsuario.ADMIN).allowed());
    }

    @Test
    void instructorPanel_deniesCliente() {
        var decision = policy.evaluate("/web/cursos/instructor", RolUsuario.CLIENTE);

        assertFalse(decision.allowed());
        assertTrue(decision.requiredRoleLabel().contains("INSTRUCTOR"));
    }

    @Test
    void comprarRoute_allowsOnlyCliente() {
        assertTrue(policy.evaluate("/web/cursos/comprar/10", RolUsuario.CLIENTE).allowed());
        assertFalse(policy.evaluate("/web/cursos/comprar/10", RolUsuario.ADMIN).allowed());
        assertFalse(policy.evaluate("/web/cursos/comprar/10", RolUsuario.INSTRUCTOR).allowed());
    }

    @Test
    void unknownWebRoute_isAllowedForAuthenticatedUser() {
        var decision = policy.evaluate("/web/cursos/123", RolUsuario.CLIENTE);

        assertTrue(decision.allowed());
    }

    @Test
    void longestRuleMatch_isAppliedForNestedAdminPath() {
        var decision = policy.evaluate("/web/cursos/admin/destacado/9", RolUsuario.CLIENTE);

        assertFalse(decision.allowed());
        assertTrue(decision.requiredRoleLabel().contains("ADMIN"));
    }
}
