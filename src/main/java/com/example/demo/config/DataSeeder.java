/* Martin Villagra Tejerina - 1 DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.config;

import java.nio.charset.StandardCharsets;
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private static final String CLIENTE_EMAIL = "alumno.demo@lotharcourses.local";
    private static final String CLIENTE_PASSWORD = "cliente123";
    private static final String INSTRUCTOR_NAME = "Alejo Testa";
    private static final String INSTRUCTOR_EMAIL = "instructor@lotharcourses.local";
    private static final String INSTRUCTOR_PASSWORD = "instructor123";
    private static final String ADMIN_NAME = "Admin Lothar Courses";
    private static final String ADMIN_EMAIL = "admin@lotharcourses.local";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String INSTRUCTOR_RECUPERADO = "Pendiente de reasignar";

    private static final Set<String> TITULOS_DESTACADOS_SEMANA = Set.of(
        "Grandes preguntas del universo",
        "Instructor de vuelo: de privado a comercial",
        "El pasado que explica todo"
    );

    private static final Set<String> TITULOS_ELIMINADOS = Set.of(
        "Idiomas y compromiso social global",
        "Liderazgo, empresa y derecho"
    );

    @Value("${app.seed.force-course-sync:false}")
    private boolean forceCourseSync;

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
        CategoriaRepository categoriaRepository,
        CursoRepository cursoRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.categoriaRepository = categoriaRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        asegurarUsuariosBase();

        List<Categoria> categorias = categoriaRepository.findAll();

        Categoria ciencia = buscarOCrearCategoria(categorias, "Ciencia y Pensamiento", "Explora ideas cientificas, filosofia y preguntas fundamentales");
        Categoria humor = buscarOCrearCategoria(categorias, "Humor y Creatividad", "Humor aplicado al lenguaje, observacion social y pensamiento critico");
        buscarOCrearCategoria(categorias, "Educacion e Idiomas", "Formacion academica, idiomas y compromiso social");
        Categoria tecnologia = buscarOCrearCategoria(categorias, "Tecnologia y Sistemas", "Impacto de la tecnologia en organizaciones y educacion superior");
        Categoria disenoProgramacion = buscarOCrearCategoria(categorias, "Diseno y Programacion", "Diseno digital, desarrollo web y programacion orientada a proyectos");
        Categoria literatura = buscarOCrearCategoria(categorias, "Literatura y Escritura", "Escritura, analisis cultural y pensamiento contemporaneo");
        Categoria hoteleria = buscarOCrearCategoria(categorias, "Hoteleria y Servicio", "Gestion hotelera, liderazgo de equipos y excelencia operativa");
        buscarOCrearCategoria(categorias, "Gastronomia y Oficio", "Cocina profesional, parrilla y cultura del trabajo");
        buscarOCrearCategoria(categorias, "Liderazgo y Derecho", "Gestion comunitaria, empresa y mirada juridica internacional");
        Categoria marketing = buscarOCrearCategoria(categorias, "Marketing y Analisis Internacional", "Estrategia comercial, analisis y relaciones internacionales");
        Categoria aviacion = buscarOCrearCategoria(categorias, "Aviacion y Operaciones de Vuelo", "Formacion para pilotaje comercial, seguridad y toma de decisiones");
        Categoria fisioterapia = buscarOCrearCategoria(categorias, "Salud y Fisioterapia", "Bienestar fisico, readaptacion funcional y prevencion de lesiones");
        Categoria gestionProyectos = buscarOCrearCategoria(categorias, "Gestion de Proyectos Global", "Planificacion, liderazgo de equipos y ejecucion de proyectos internacionales");

        Categoria historia = buscarOCrearCategoria(categorias, "Historia y Sociedad", "Procesos historicos, memoria colectiva y comprension del presente");
        Categoria cineActuacion = buscarOCrearCategoria(categorias, "Cine y Actuacion", "Actuacion, lenguaje audiovisual y construccion de personajes");
        Categoria arquitectura = buscarOCrearCategoria(categorias, "Arquitectura y Urbanismo", "Proyecto, obra y diseno de espacios habitables");
        Categoria culturaPopNarrativa = buscarOCrearCategoria(categorias, "Cultura Pop y Narrativa", "Universos narrativos, fandom y analisis cultural");
        Categoria comunicacion = buscarOCrearCategoria(categorias, "Comunicacion y Periodismo", "Locucion, periodismo digital y comunicacion audiovisual");

        fusionarCategoriasRelacionadas(
            disenoProgramacion,
            Arrays.asList("Diseno", "Diseno Web", "Programacion", "Desarrollo Web", "Frontend", "Backend")
        );

        List<Curso> cursosExistentes = cursoRepository.findAll();
        if (!cursosExistentes.isEmpty() && !forceCourseSync) {
            limpiarCursosTemporalesRecuperados();
            return;
        }

        List<Curso> cursos = new ArrayList<>();
        cursos.add(crearCurso("Grandes preguntas del universo", "Ciencia, pensamiento y las grandes preguntas del universo explicadas con claridad.", "Juan Martin Maldacena", "/uploads/images/img-027.png", "/uploads/videos/vid-004.mp4", ciencia));
        cursos.add(crearCurso("Humor con mirada critica", "Humor, lenguaje y creatividad: como se construye una mirada critica desde la risa.", "Yayo Guridi", "/uploads/images/img-010.png", "/uploads/videos/vid-010.mp4", humor));
        cursos.add(crearCurso("Tecnologia en la educacion superior", "Sistemas, educacion superior y el rol de la tecnologia en la formacion academica.", "Fernando Javier Villagra", "/uploads/images/img-019.jpg", "/uploads/videos/vid-009.mp4", tecnologia));
        cursos.add(crearCurso("Escritura para entender el presente", "Escritura, pensamiento y literatura como herramienta para entender el presente.", "Rosa Montero", "/uploads/images/img-003.png", "/uploads/videos/vid-024.mp4", literatura));
        cursos.add(crearCurso("Gestion hotelera con excelencia", "Experiencia real en hoteleria premiada, gestion de equipos y excelencia en servicio.", "Christine Fox & David Mcghie", "/uploads/images/img-032.jpg", "/uploads/videos/vid-026.mp4", hoteleria));
        cursos.add(crearCurso("Marketing aplicado al mundo real", "Marketing, analisis y relaciones internacionales aplicadas al mundo real.", "Francesco Nicola Bute", "/uploads/images/img-002.png", "/uploads/videos/vid-011.mp4", marketing));
        cursos.add(crearCurso("Instructor de vuelo: de privado a comercial", "Entrenamiento integral con enfoque operativo: navegacion, meteorologia, CRM y toma de decisiones para pilotos privados y comerciales.", "Alejo Testa", "/uploads/images/img-004.jpg", "/uploads/videos/vid-002.mp4", aviacion));
        cursos.add(crearCurso("El pasado que explica todo", "Descubre procesos historicos que construyen el presente desde una mirada critica y bien documentada.", "Nils Jacobsen", "/uploads/images/img-008.png", "/uploads/videos/vid-006.mp4", historia));
        cursos.add(crearCurso("Fisioterapia funcional para la vida diaria", "Movilidad, prevencion de lesiones y ejercicios funcionales para mejorar la calidad de vida.", "Tu mejor amigo - Fisioterapeuta", "/uploads/images/img-014.png", "/uploads/videos/vid-001.mp4", fisioterapia));
        cursos.add(crearCurso("Gestion de proyectos de inicio a cierre", "Metodologias, planificacion y seguimiento para liderar proyectos con alcance global.", "Juan de Dios", "/uploads/images/img-006.jpeg", "/uploads/videos/vid-014.mp4", gestionProyectos));
        cursos.add(crearCurso("Locucion, periodismo y comunicacion digital", "Del microfono a la pantalla: formacion en locucion nacional, periodismo digital y comunicacion audiovisual contemporanea.", "Mariano Cardarelli", "/uploads/images/img-031.jpg", "/uploads/videos/vid-025.mp4", comunicacion));
        cursos.add(crearCurso("Operaciones IFR y gestion de cabina", "Formacion avanzada en vuelo instrumental, procedimientos IFR y gestion eficiente de cabina en operaciones aereas exigentes.", "Alejo Testa", "/uploads/images/img-016.jpeg", "/uploads/videos/vid-008.mp4", aviacion));
        cursos.add(crearCurso("Actuacion, comedia y construccion de personajes", "La tecnica actoral vista desde la comedia, el cine y el teatro: como crear personajes autenticos y memorables.", "Guillermo Francella", "/uploads/images/img-026.png", "/uploads/videos/vid-022.mp4", cineActuacion));
        cursos.add(crearCurso("Arquitectura, proyecto y direccion de obra", "Diseno arquitectonico, direccion de proyectos y supervision de obra desde la practica profesional real.", "Giuliano Bute", "/uploads/images/img-030.png", "/uploads/videos/vid-029.mp4", arquitectura));
        cursos.add(crearCurso("Harry Potter: secretos, historia y coleccionismo", "Descubre los secretos del universo de Harry Potter, su historia detras de escenas y la cultura del coleccionismo que mueve a millones de fans.", "Patricio Tarantino", "/uploads/images/img-001.png", "/uploads/videos/vid-018.mp4", culturaPopNarrativa));

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
            "el poder de la aviacion ejecutiva"
        ));

        Set<String> legacyNormalizados = titulosLegacy.stream().map(this::normalizarTexto).collect(Collectors.toSet());
        Set<String> eliminadosNormalizados = TITULOS_ELIMINADOS.stream().map(this::normalizarTexto).collect(Collectors.toSet());

        List<Curso> cursosAEliminar = cursosExistentes.stream()
            .filter(c -> legacyNormalizados.contains(normalizarTexto(c.getTitulo()))
                || eliminadosNormalizados.contains(normalizarTexto(c.getTitulo())))
            .toList();

        if (!cursosAEliminar.isEmpty()) {
            cursoRepository.deleteAll(cursosAEliminar);
        }

        eliminarDuplicadosPorTituloNormalizado();

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
                existente.setTitulo(cursoNuevo.getTitulo());
                existente.setDescripcion(cursoNuevo.getDescripcion());
                existente.setInstructor(cursoNuevo.getInstructor());
                existente.setImagenUrl(cursoNuevo.getImagenUrl());
                existente.setVideoUrl(cursoNuevo.getVideoUrl());
                existente.setCategoria(cursoNuevo.getCategoria());
                cursosAGuardar.add(existente);
            } else {
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

    private void eliminarDuplicadosPorTituloNormalizado() {
        List<Curso> cursos = cursoRepository.findAll();
        Map<String, List<Curso>> porTitulo = cursos.stream().collect(Collectors.groupingBy(c -> normalizarTexto(c.getTitulo())));

        List<Curso> duplicados = new ArrayList<>();
        for (List<Curso> grupo : porTitulo.values()) {
            if (grupo.size() <= 1) {
                continue;
            }

            grupo.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            duplicados.addAll(grupo.subList(1, grupo.size()));
        }

        if (!duplicados.isEmpty()) {
            cursoRepository.deleteAll(duplicados);
        }
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return "";
        }

        String reparado = repararMojibake(valor);
        String sinAcentos = Normalizer.normalize(reparado, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    private String repararMojibake(String valor) {
        if (valor == null || valor.isBlank()) {
            return valor;
        }

        if (valor.contains("Ãƒ") || valor.contains("Ã‚")) {
            return new String(valor.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replace("Ã‚", "");
        }

        return valor;
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

            boolean debeFusionarse = aliases.stream().map(this::normalizarTexto).anyMatch(alias -> alias.equals(nombreNormalizado));
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
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }
}
