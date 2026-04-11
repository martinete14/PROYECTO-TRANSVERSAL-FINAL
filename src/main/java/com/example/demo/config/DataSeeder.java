/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.config;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.UsuarioRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String CLIENTE_NAME = "Alumno Demo";
    private static final String CLIENTE_EMAIL = "alumno.demo@miniacademia.local";
    private static final String CLIENTE_PASSWORD = "cliente123";
    private static final String INSTRUCTOR_NAME = "Alejo Testa";
    private static final String INSTRUCTOR_EMAIL = "instructor@miniacademia.local";
    private static final String INSTRUCTOR_PASSWORD = "instructor123";
    private static final String ADMIN_NAME = "Admin MiniAcademia";
    private static final String ADMIN_EMAIL = "admin@miniacademia.local";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String INSTRUCTOR_RECUPERADO = "Pendiente de reasignar";
    private static final Set<String> TITULOS_DESTACADOS_SEMANA = Set.of(
        "Grandes preguntas del universo",
        "Instructor de vuelo: de privado a comercial",
        "El pasado que explica todo"
    );

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public DataSeeder(
        CategoriaRepository categoriaRepository,
        CursoRepository cursoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        asegurarUsuariosBase();

        List<Categoria> categorias = categoriaRepository.findAll();

        Categoria ciencia = buscarOCrearCategoria(categorias, "Ciencia y Pensamiento", "Explora ideas científicas, filosofía y preguntas fundamentales");
        Categoria humor = buscarOCrearCategoria(categorias, "Humor y Creatividad", "Humor aplicado al lenguaje, observación social y pensamiento crítico");
        Categoria educacion = buscarOCrearCategoria(categorias, "Educación e Idiomas", "Formación académica, idiomas y compromiso social");
        Categoria tecnologia = buscarOCrearCategoria(categorias, "Tecnología y Sistemas", "Impacto de la tecnología en organizaciones y educación superior");
        Categoria disenoProgramacion = buscarOCrearCategoria(categorias, "Diseño y Programación", "Diseño digital, desarrollo web y programación orientada a proyectos");
        Categoria literatura = buscarOCrearCategoria(categorias, "Literatura y Escritura", "Escritura, analisis cultural y pensamiento contemporaneo");
        Categoria hoteleria = buscarOCrearCategoria(categorias, "Hotelería y Servicio", "Gestión hotelera, liderazgo de equipos y excelencia operativa");
        buscarOCrearCategoria(categorias, "Gastronomía y Oficio", "Cocina profesional, parrilla y cultura del trabajo");
        Categoria liderazgo = buscarOCrearCategoria(categorias, "Liderazgo y Derecho", "Gestión comunitaria, empresa y mirada jurídica internacional");
        Categoria marketing = buscarOCrearCategoria(categorias, "Marketing y Análisis Internacional", "Estrategia comercial, análisis y relaciones internacionales");
        Categoria aviacion = buscarOCrearCategoria(categorias, "Aviación y Operaciones de Vuelo", "Formación para pilotaje comercial, seguridad y toma de decisiones");
        Categoria fisioterapia = buscarOCrearCategoria(categorias, "Salud y Fisioterapia", "Bienestar físico, readaptación funcional y prevención de lesiones");
        Categoria gestionProyectos = buscarOCrearCategoria(categorias, "Gestión de Proyectos Global", "Planificación, liderazgo de equipos y ejecución de proyectos internacionales");

        // Categorias adicionales para ampliar el catalogo y mejorar el filtro principal.
        Categoria historia = buscarOCrearCategoria(categorias, "Historia y Sociedad", "Procesos historicos, memoria colectiva y comprension del presente");
        buscarOCrearCategoria(categorias, "Cine y Actuación", "Actuacion, lenguaje audiovisual y construccion de personajes");
        buscarOCrearCategoria(categorias, "Arquitectura y Urbanismo", "Proyecto, obra y diseño de espacios habitables");
        buscarOCrearCategoria(categorias, "Cultura Pop y Narrativa", "Universos narrativos, fandom y analisis cultural");

        fusionarCategoriasRelacionadas(
            disenoProgramacion,
            Arrays.asList("Diseño", "Diseno", "Diseño Web", "Diseno Web", "Programación", "Programacion", "Desarrollo Web", "Frontend", "Backend")
        );

        List<Curso> cursos = new ArrayList<>();
        cursos.add(crearCurso("Grandes preguntas del universo", "Ciencia, pensamiento y las grandes preguntas del universo explicadas con claridad.", "Juan Martín Maldacena", "/uploads/images/0f6b3ce5-7705-415f-900a-1815fc003a2b.png", "/uploads/videos/17611120-11db-4ea2-a9ce-0b2f1a4d68ce.mp4", ciencia));
        cursos.add(crearCurso("Humor con mirada crítica", "Humor, lenguaje y creatividad: cómo se construye una mirada crítica desde la risa.", "Yayo Guridi", "/uploads/images/1606bd28-708c-4368-ac78-99ec75d061da.jpg", "/uploads/videos/265fbdc9-0190-48be-a918-8117bcf7ed6d.mp4", humor));
        cursos.add(crearCurso("Idiomas y compromiso social global", "Educación, idiomas y compromiso social desde la experiencia rotaria internacional.", "Victoria Tejerina Allende", "/uploads/images/16c1a8e0-8b86-4b69-850c-01993e72c26e.png", "/uploads/videos/32dd16b3-849f-4b2b-9026-c9fc03355376.mp4", educacion));
        cursos.add(crearCurso("Tecnología en la educación superior", "Sistemas, educación superior y el rol de la tecnología en la formación académica.", "Fernando Javier Villagra", "/uploads/images/1e776772-4fd3-48f7-b452-b5e537197a18.png", "/uploads/videos/33dfd196-8ec9-466b-84e8-0010f745c6ac.mp4", tecnologia));
        cursos.add(crearCurso("Escritura para entender el presente", "Escritura, pensamiento y literatura como herramienta para entender el presente.", "Rosa Montero", "/uploads/images/22799d26-6f56-46a9-b907-1a80edc3d342.jpg", "/uploads/videos/375c3e43-b0e3-4f73-8bd8-b181f39570b7.mp4", literatura));
        cursos.add(crearCurso("Gestión hotelera con excelencia", "Experiencia real en hotelería premiada, gestión de equipos y excelencia en servicio.", "Christine Fox & David Mcghie", "/uploads/images/23a3e63e-0d7a-47eb-bf4f-91664c636557.png", "/uploads/videos/3c9080f7-5558-42c2-8a9b-5bf25d8652e4.mp4", hoteleria));
        cursos.add(crearCurso("Liderazgo, empresa y derecho", "Liderazgo comunitario, empresa y derecho desde una mirada internacional.", "Mel Powell", "/uploads/images/25eeda1c-aef5-4734-b120-7d986390e62f.jpeg", "/uploads/videos/560fd961-ad95-4e91-b135-e31133d13e53.mp4", liderazgo));
        cursos.add(crearCurso("Marketing aplicado al mundo real", "Marketing, análisis y relaciones internacionales aplicadas al mundo real.", "Francesco Nicola Bute", "/uploads/images/323a11b0-7be1-4d3e-982d-ad1821baa1c0.jpg", "/uploads/videos/5f0e3e9b-5bbc-4683-b3b0-2d000cb12d2e.mp4", marketing));
        cursos.add(crearCurso("Instructor de vuelo: de privado a comercial", "Entrenamiento integral con enfoque operativo: navegación, meteorología, CRM y toma de decisiones para pilotos privados y comerciales.", "Alejo Testa", "/uploads/images/3f79b893-54f0-476f-8525-ebd15ac2589c.png", "/uploads/videos/5fc7cdce-2732-47fa-acf8-7b92144c406d.mp4", aviacion));
        cursos.add(crearCurso("El pasado que explica todo", "Descubre procesos historicos que construyen el presente desde una mirada critica y bien documentada.", "Nils Jacobsen", "/uploads/images/482e1a87-e046-4e82-b0c2-400cd4bc8236.png", "/uploads/videos/63e7e9e3-002a-47bc-898c-1c703681b8a9.mp4", historia));
        cursos.add(crearCurso("Fisioterapia funcional para la vida diaria", "Movilidad, prevención de lesiones y ejercicios funcionales para mejorar la calidad de vida.", "Tu mejor amigo - Fisioterapeuta", "/uploads/images/4e931701-5b7a-4682-8913-d3eaffc57bcd.jpg", "/uploads/videos/69052cb3-cca5-46dd-ac7c-01734bc3a178.mp4", fisioterapia));
        cursos.add(crearCurso("Gestión de proyectos de inicio a cierre", "Metodologías, planificación y seguimiento para liderar proyectos con alcance global.", "Juan de Dios", "/uploads/images/536a64bf-112e-4e05-a672-c8c0617ecf6c.png", "/uploads/videos/86584f29-d803-42fb-923c-b0cd4be83444.mp4", gestionProyectos));

        Set<String> titulosLegacy = new HashSet<>(Arrays.asList(
            "java spring boot desde cero",
            "sql para proyectos reales",
            "diseno ui moderno",
            "patrones de arquitectura",
            "mysql optimizacion",
            "bootstrap 5 avanzado",
            "apis rest con spring",
            "modelado er y relacional",
            "seo tecnico para proyectos web",
            "proyecto full stack dam/daw",
            "humor con mirada critica",
            "tecnologia en la educacion superior",
            "gestion hotelera con excelencia",
            "parrilla y cultura del oficio",
            "operaciones ifr y gestion de cabina",
            "el poder de la aviacion ejecutiva"
        ));

        List<Curso> cursosExistentes = cursoRepository.findAll();
        List<Curso> cursosLegacy = cursosExistentes.stream()
            .filter(c -> titulosLegacy.contains(c.getTitulo().toLowerCase(Locale.ROOT)))
            .toList();

        if (!cursosLegacy.isEmpty()) {
            cursoRepository.deleteAll(cursosLegacy);
        }

        // Actualizar cursos existentes o crear nuevos
        List<Curso> cursosActuales = cursoRepository.findAll();
        Map<String, Curso> cursosPorTituloNormalizado = new HashMap<>();
        for (Curso curso : cursosActuales) {
            cursosPorTituloNormalizado.put(normalizarTexto(curso.getTitulo()), curso);
        }

        List<Curso> cursosAGuardar = new ArrayList<>();
        for (Curso cursoNuevo : cursos) {
            String tituloNormalizado = normalizarTexto(cursoNuevo.getTitulo());
            Curso existente = cursosPorTituloNormalizado.get(tituloNormalizado);
            
            if (existente != null) {
                // Actualizar curso existente con nuevas URLs
                existente.setDescripcion(cursoNuevo.getDescripcion());
                existente.setInstructor(cursoNuevo.getInstructor());
                existente.setImagenUrl(cursoNuevo.getImagenUrl());
                existente.setVideoUrl(cursoNuevo.getVideoUrl());
                existente.setCategoria(cursoNuevo.getCategoria());
                cursosAGuardar.add(existente);
            } else {
                // Crear nuevo curso
                cursosAGuardar.add(cursoNuevo);
            }
        }

        if (!cursosAGuardar.isEmpty()) {
            cursoRepository.saveAll(cursosAGuardar);
        }

        limpiarCursosTemporalesRecuperados();

        aplicarDestacadosFijos();
    }

    private Categoria buscarOCrearCategoria(List<Categoria> categorias, String nombre, String descripcion) {
        return categorias.stream()
                .filter(c -> normalizarTexto(c.getNombre()).equals(normalizarTexto(nombre)))
                .findFirst()
                .map(existente -> {
                    if (!Objects.equals(existente.getNombre(), nombre) || !Objects.equals(existente.getDescripcion(), descripcion)) {
                        existente.setNombre(nombre);
                        existente.setDescripcion(descripcion);
                        return categoriaRepository.save(existente);
                    }
                    return existente;
                })
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(nombre);
                    nueva.setDescripcion(descripcion);
                    return categoriaRepository.save(nueva);
                });
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return "";
        }

        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    private Curso crearCurso(String titulo, String descripcion, String instructor, String imagenUrl, String videoUrl, Categoria categoria) {
        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);
        curso.setImagenUrl(imagenUrl);
        curso.setVideoUrl(videoUrl);
        curso.setCategoria(categoria);
        return curso;
    }

    private void fusionarCategoriasRelacionadas(Categoria categoriaObjetivo, List<String> aliases) {
        String objetivoNormalizado = normalizarTexto(categoriaObjetivo.getNombre());

        List<Categoria> categoriasActuales = categoriaRepository.findAll();
        for (Categoria candidata : categoriasActuales) {
            String nombreNormalizado = normalizarTexto(candidata.getNombre());
            if (nombreNormalizado.equals(objetivoNormalizado)) {
                continue;
            }

            boolean debeFusionarse = aliases.stream()
                .map(this::normalizarTexto)
                .anyMatch(alias -> alias.equals(nombreNormalizado));

            if (!debeFusionarse) {
                continue;
            }

            List<Curso> cursosDeCategoria = cursoRepository.findByCategoriaId(candidata.getId());
            for (Curso curso : cursosDeCategoria) {
                curso.setCategoria(categoriaObjetivo);
            }

            if (!cursosDeCategoria.isEmpty()) {
                cursoRepository.saveAll(cursosDeCategoria);
            }

            categoriaRepository.delete(candidata);
        }
    }

    private void limpiarCursosTemporalesRecuperados() {
        List<Curso> cursosTemporales = cursoRepository.findAll().stream()
            .filter(curso -> (curso.getTitulo() != null && curso.getTitulo().startsWith("Contenido recuperado"))
                || INSTRUCTOR_RECUPERADO.equals(curso.getInstructor()))
            .toList();

        if (!cursosTemporales.isEmpty()) {
            cursoRepository.deleteAll(cursosTemporales);
        }
    }

    private void aplicarDestacadosFijos() {
        List<Curso> cursos = cursoRepository.findAll();
        List<Curso> actualizados = cursos.stream()
            .filter(curso -> {
                boolean destacado = TITULOS_DESTACADOS_SEMANA.contains(curso.getTitulo());
                if (curso.isDestacadoSemana() == destacado) {
                    return false;
                }
                curso.setDestacadoSemana(destacado);
                return true;
            })
            .collect(Collectors.toList());

        if (!actualizados.isEmpty()) {
            cursoRepository.saveAll(actualizados);
        }
    }

    private void asegurarUsuariosBase() {
        asegurarUsuario(CLIENTE_NAME, CLIENTE_EMAIL, CLIENTE_PASSWORD, "CLIENTE");
        asegurarUsuario(INSTRUCTOR_NAME, INSTRUCTOR_EMAIL, INSTRUCTOR_PASSWORD, "INSTRUCTOR");
        asegurarUsuario(ADMIN_NAME, ADMIN_EMAIL, ADMIN_PASSWORD, "ADMIN");
    }

    private void asegurarUsuario(String nombre, String email, String password, String rol) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElseGet(Usuario::new);

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }
}
