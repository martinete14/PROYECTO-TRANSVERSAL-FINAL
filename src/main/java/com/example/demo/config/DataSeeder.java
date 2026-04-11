/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.config;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
        cursos.add(crearCurso("Grandes preguntas del universo", "Ciencia, pensamiento y las grandes preguntas del universo explicadas con claridad.", "Juan Martín Maldacena", "https://images.unsplash.com/photo-1507413245164-6160d8298b31", "https://cdn.coverr.co/videos/coverr-space-stars-1608/1080p.mp4", ciencia));
        cursos.add(crearCurso("Humor con mirada crítica", "Humor, lenguaje y creatividad: cómo se construye una mirada crítica desde la risa.", "Yayo Guridi", "https://images.unsplash.com/photo-1521791136064-7986c2920216", "https://cdn.coverr.co/videos/coverr-laughing-friends-5174/1080p.mp4", humor));
        cursos.add(crearCurso("Idiomas y compromiso social global", "Educación, idiomas y compromiso social desde la experiencia rotaria internacional.", "Victoria Tejerina Allende", "https://images.unsplash.com/photo-1523240795612-9a054b0db644", "https://cdn.coverr.co/videos/coverr-group-study-session-1579/1080p.mp4", educacion));
        cursos.add(crearCurso("Tecnología en la educación superior", "Sistemas, educación superior y el rol de la tecnología en la formación académica.", "Fernando Javier Villagra", "https://images.unsplash.com/photo-1518779578993-ec3579fee39f", "https://cdn.coverr.co/videos/coverr-team-meeting-1578/1080p.mp4", tecnologia));
        cursos.add(crearCurso("Escritura para entender el presente", "Escritura, pensamiento y literatura como herramienta para entender el presente.", "Rosa Montero", "https://images.unsplash.com/photo-1455390582262-044cdead277a", "https://cdn.coverr.co/videos/coverr-writing-in-a-notebook-5175/1080p.mp4", literatura));
        cursos.add(crearCurso("Gestión hotelera con excelencia", "Experiencia real en hotelería premiada, gestión de equipos y excelencia en servicio.", "Christine Fox & David Mcghie", "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa", "https://cdn.coverr.co/videos/coverr-hotel-lobby-5182/1080p.mp4", hoteleria));
        cursos.add(crearCurso("Liderazgo, empresa y derecho", "Liderazgo comunitario, empresa y derecho desde una mirada internacional.", "Mel Powell", "https://images.unsplash.com/photo-1552664730-d307ca884978", "https://cdn.coverr.co/videos/coverr-business-team-5179/1080p.mp4", liderazgo));
        cursos.add(crearCurso("Marketing aplicado al mundo real", "Marketing, análisis y relaciones internacionales aplicadas al mundo real.", "Francesco Nicola Bute", "https://images.unsplash.com/photo-1552664730-d307ca884978", "https://cdn.coverr.co/videos/coverr-marketing-meeting-5178/1080p.mp4", marketing));
        cursos.add(crearCurso("Instructor de vuelo: de privado a comercial", "Entrenamiento integral con enfoque operativo: navegación, meteorología, CRM y toma de decisiones para pilotos privados y comerciales.", "Alejo Testa", "https://images.unsplash.com/photo-1436491865332-7a61a109cc05", "https://cdn.coverr.co/videos/coverr-plane-taking-off-5180/1080p.mp4", aviacion));
        cursos.add(crearCurso("El pasado que explica todo", "Descubre procesos historicos que construyen el presente desde una mirada critica y bien documentada.", "Nils Jacobsen", "https://images.unsplash.com/photo-1461360228754-6e81c478b882", "https://cdn.coverr.co/videos/coverr-man-reading-a-book-5306/1080p.mp4", historia));
        cursos.add(crearCurso("Fisioterapia funcional para la vida diaria", "Movilidad, prevención de lesiones y ejercicios funcionales para mejorar la calidad de vida.", "Tu mejor amigo - Fisioterapeuta", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b", "https://cdn.coverr.co/videos/coverr-doctor-checking-a-patient-1574/1080p.mp4", fisioterapia));
        cursos.add(crearCurso("Gestión de proyectos de inicio a cierre", "Metodologías, planificación y seguimiento para liderar proyectos con alcance global.", "Juan de Dios", "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40", "https://cdn.coverr.co/videos/coverr-meeting-in-an-office-1577/1080p.mp4", gestionProyectos));

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

        Set<String> titulosActuales = new HashSet<>();
        cursoRepository.findAll().forEach(c -> titulosActuales.add(c.getTitulo().toLowerCase(Locale.ROOT)));

        List<Curso> cursosNuevos = cursos.stream()
                .filter(c -> !titulosActuales.contains(c.getTitulo().toLowerCase(Locale.ROOT)))
                .toList();

        if (!cursosNuevos.isEmpty()) {
            cursoRepository.saveAll(cursosNuevos);
        }

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
