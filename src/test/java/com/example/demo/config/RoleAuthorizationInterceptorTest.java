package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

class RoleAuthorizationInterceptorTest {

    private RoleAuthorizationInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new RoleAuthorizationInterceptor(new RoutePermissionPolicy());
    }

    @Test
    void nonWebPath_allowsRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
    }

    @Test
    void unauthenticatedWebPath_redirectsToLogin() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/web/cursos");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertTrue(response.getRedirectedUrl().startsWith("/web/auth/login"));
    }

    @Test
    void authPaths_areAlwaysAllowed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/web/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
    }

    @Test
    void clienteTryingAdmin_redirectsToDeniedWithContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/web/cursos/admin");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, 10L);
        session.setAttribute("AUTH_ROLE", "CLIENTE");
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        String redirect = response.getRedirectedUrl();
        assertTrue(redirect.startsWith("/web/auth/denegado"));
        assertTrue(redirect.contains("required="));
        assertTrue(redirect.contains("from="));
    }

    @Test
    void instructorOnInstructorPanel_isAllowed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/web/cursos/instructor");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, 10L);
        session.setAttribute("AUTH_ROLE", "INSTRUCTOR");
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
    }

    @Test
    void instructorOnComprar_isDenied() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/web/cursos/comprar/2");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, 10L);
        session.setAttribute("AUTH_ROLE", "INSTRUCTOR");
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertTrue(response.getRedirectedUrl().startsWith("/web/auth/denegado"));
    }
}
