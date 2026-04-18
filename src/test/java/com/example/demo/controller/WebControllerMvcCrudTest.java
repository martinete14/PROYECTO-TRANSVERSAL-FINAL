package com.example.demo.controller;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.config.AuthSessionKeys;
import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class WebControllerMvcCrudTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @BeforeEach
    void setUpMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void crudCompletoDesdeWeb_adminPuedeCrearEditarYEliminarCurso() throws Exception {
        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");
        Long categoriaId = obtenerCategoriaIdValida();
        String seed = String.valueOf(System.currentTimeMillis());
        String tituloInicial = "Curso MVC " + seed;
        String tituloActualizado = "Curso MVC actualizado " + seed;

        mockMvc.perform(multipart("/web/cursos/crear")
                .session(adminSession)
                .param("titulo", tituloInicial)
                .param("descripcion", "Descripcion de prueba suficientemente larga para validar un alta completa desde la capa web.")
                .param("instructor", "Admin QA")
                .param("categoriaId", String.valueOf(categoriaId)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/web/cursos"));

        Curso creado = buscarCursoPorTitulo(tituloInicial)
            .orElseThrow(() -> new AssertionError("No se encontro el curso recien creado"));

        mockMvc.perform(post("/web/cursos/actualizar/{id}", creado.getId())
                .session(adminSession)
                .param("titulo", tituloActualizado)
                .param("descripcion", "Descripcion actualizada y persistente para validar que la edicion desde admin queda guardada correctamente.")
                .param("instructor", "Admin QA")
                .param("categoriaId", String.valueOf(categoriaId))
                .param("imagenUrl", creado.getImagenUrl() == null ? "" : creado.getImagenUrl())
                .param("videoUrl", creado.getVideoUrl() == null ? "" : creado.getVideoUrl()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/web/cursos"));

        Curso actualizado = cursoRepository.findById(creado.getId())
            .orElseThrow(() -> new AssertionError("No se encontro el curso actualizado"));

        if (!tituloActualizado.equals(actualizado.getTitulo())) {
            throw new AssertionError("El titulo no se actualizo en base de datos");
        }

        mockMvc.perform(post("/web/cursos/eliminar/{id}", creado.getId())
                .session(adminSession))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/web/cursos"));

        if (cursoRepository.existsById(creado.getId())) {
            throw new AssertionError("El curso no se elimino desde el flujo web");
        }
    }

    @Test
    void nuevoCurso_adminPuedeAbrirFormularioSinError500() throws Exception {
        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");

        mockMvc.perform(get("/web/cursos/nuevo").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("crear-curso"))
            .andExpect(model().attributeExists("formData"))
            .andExpect(model().attributeExists("categorias"));
    }

    @Test
    void crearCursoConDatosInvalidosMuestraErroresPorCampo() throws Exception {
        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");

        mockMvc.perform(multipart("/web/cursos/crear")
                .session(adminSession)
                .param("titulo", "abc")
                .param("descripcion", "corta")
                .param("instructor", "a")
                .param("imagenUrl", "notaurl")
                .param("videoUrl", "https://example.com/video.webm")
                .param("categoriaId", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("crear-curso"))
            .andExpect(model().attributeExists("fieldErrors"))
            .andExpect(model().attribute("fieldErrors", hasKey("titulo")))
            .andExpect(model().attribute("fieldErrors", hasKey("descripcion")))
            .andExpect(model().attribute("fieldErrors", hasKey("instructor")))
            .andExpect(model().attribute("fieldErrors", hasKey("categoriaId")))
            .andExpect(model().attribute("fieldErrors", hasKey("imagenUrl")))
            .andExpect(model().attribute("fieldErrors", hasKey("videoUrl")));
    }

    @Test
    void panelesAdminEInstructorPermitenBuscarPorTitulo() throws Exception {
        Long categoriaId = obtenerCategoriaIdValida();
        String seed = String.valueOf(System.currentTimeMillis());
        String tituloUnico = "Filtro MVC " + seed;

        Curso curso = new Curso();
        curso.setTitulo(tituloUnico);
        curso.setDescripcion("Curso de prueba para validar filtro por titulo en panel admin e instructor.");
        curso.setInstructor("Alejo Testa");

        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new AssertionError("Categoria no disponible para test"));
        curso.setCategoria(categoria);

        cursoRepository.save(curso);

        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");
        mockMvc.perform(get("/web/cursos/admin")
                .session(adminSession)
                .param("instructor", "Alejo")
                .param("titulo", tituloUnico))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-cursos"))
            .andExpect(content().string(containsString(tituloUnico)));

        MockHttpSession instructorSession = crearSesionPorEmail("instructor@lotharcourses.local", "INSTRUCTOR");
        mockMvc.perform(get("/web/cursos/instructor")
                .session(instructorSession)
                .param("titulo", tituloUnico))
            .andExpect(status().isOk())
            .andExpect(view().name("instructor-cursos"))
            .andExpect(content().string(containsString(tituloUnico)));
    }

    @Test
    void adminPuedeAbrirApartadoLogs() throws Exception {
        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");

        mockMvc.perform(get("/web/cursos/admin/logs").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-logs"))
            .andExpect(model().attributeExists("logs"));
    }

    @Test
    void usuarioAutenticadoPuedeAbrirPerfil() throws Exception {
        MockHttpSession adminSession = crearSesionPorEmail("admin@lotharcourses.local", "ADMIN");

        mockMvc.perform(get("/web/perfil").session(adminSession))
            .andExpect(status().isOk())
            .andExpect(view().name("perfil"))
            .andExpect(model().attributeExists("usuario"))
            .andExpect(model().attributeExists("identificadorPerfil"));
    }

    @Test
    void usuarioPuedeActualizarSuPerfil() throws Exception {
        MockHttpSession alumnoSession = crearSesionPorEmail("alumno.demo@lotharcourses.local", "CLIENTE");

        mockMvc.perform(post("/web/perfil/guardar")
                .session(alumnoSession)
                .param("nombre", "Alumno Perfil QA")
                .param("documento", "X1234567")
                .param("telefono", "+34600111222")
                .param("fechaNacimiento", "2000-01-15")
                .param("ciudadPais", "Madrid, Espana")
                .param("institucionAcademica", "Lothar Courses")
                .param("programaAcademico", "DAW")
                .param("nivelAcademico", "Intermedio")
                .param("fotoPerfilUrl", "https://example.com/perfil.jpg")
                .param("biografia", "Perfil academico de prueba para validar guardado."))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/web/perfil"));

        Usuario actualizado = usuarioRepository.findByEmailIgnoreCase("alumno.demo@lotharcourses.local")
            .orElseThrow(() -> new AssertionError("Usuario de prueba no encontrado"));

        if (!"Alumno Perfil QA".equals(actualizado.getNombre())) {
            throw new AssertionError("No se actualizo el nombre del perfil");
        }
        if (actualizado.getStudentId() == null || actualizado.getStudentId().isBlank()) {
            throw new AssertionError("No se asigno ID de estudiante al perfil CLIENTE");
        }
    }

    private MockHttpSession crearSesionPorEmail(String email, String rol) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new AssertionError("No existe usuario para test: " + email));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthSessionKeys.AUTH_USER_ID, usuario.getId());
        session.setAttribute(AuthSessionKeys.AUTH_ROLE, rol);
        session.setAttribute(AuthSessionKeys.AUTH_NAME, usuario.getNombre());
        return session;
    }

    private Long obtenerCategoriaIdValida() {
        return categoriaRepository.findAll().stream()
            .map(Categoria::getId)
            .findFirst()
            .orElseThrow(() -> new AssertionError("No hay categorias disponibles para tests"));
    }

    private Optional<Curso> buscarCursoPorTitulo(String titulo) {
        return cursoRepository.findAll().stream()
            .filter(curso -> titulo.equals(curso.getTitulo()))
            .findFirst();
    }
}
