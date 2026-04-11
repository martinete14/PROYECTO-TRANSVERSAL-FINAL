package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class AuthControllerTest {

    @Test
    void accesoDenegado_usesProvidedContext() {
        AuthController controller = new AuthController(null);
        Model model = new ConcurrentModel();

        String view = controller.accesoDenegado("ADMIN", "/web/cursos/admin", model);

        assertEquals("acceso-denegado", view);
        assertEquals("ADMIN", model.getAttribute("requiredRoleMessage"));
        assertEquals("/web/cursos/admin", model.getAttribute("requestedPath"));
    }

    @Test
    void accesoDenegado_usesSafeDefaults() {
        AuthController controller = new AuthController(null);
        Model model = new ConcurrentModel();

        String view = controller.accesoDenegado("", null, model);

        assertEquals("acceso-denegado", view);
        assertEquals("ADMIN", model.getAttribute("requiredRoleMessage"));
        assertTrue(model.getAttribute("requestedPath").toString().startsWith("/web/cursos"));
    }
}
