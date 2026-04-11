/* MartÃ­n Villagra Tejerina - 1Â°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
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

        Categoria ciencia = buscarOCrearCategoria(categorias, "Ciencia y Pensamiento", "Explora ideas cientÃ­ficas, filosofÃ­a y preguntas fundamentales");
        Categoria humor = buscarOCrearCategoria(categorias, "Humor y Creatividad", "Humor aplicado al lenguaje, observaciÃ³n social y pensamiento crÃ­tico");
        Categoria educacion = buscarOCrearCategoria(categorias, "EducaciÃ³n e Idiomas", "FormaciÃ³n acadÃ©mica, idiomas y compromiso social");
        Categoria tecnologia = buscarOCrearCategoria(categorias, "TecnologÃ­a y Sistemas", "Impacto de la tecnologÃ­a en organizaciones y educaciÃ³n superior");
        Categoria disenoProgramacion = buscarOCrearCategoria(categorias, "DiseÃ±o y ProgramaciÃ³n", "DiseÃ±o digital, desarrollo web y programaciÃ³n orientada a proyectos");
        Categoria literatura = buscarOCrearCategoria(categorias, "Literatura y Escritura", "Escritura, analisis cultural y pensamiento contemporaneo");
        Categoria hoteleria = buscarOCrearCategoria(categorias, "HotelerÃ­a y Servicio", "GestiÃ³n hotelera, liderazgo de equipos y excelencia operativa");
        buscarOCrearCategoria(categorias, "GastronomÃ­a y Oficio", "Cocina profesional, parrilla y cultura del trabajo");
        Categoria liderazgo = buscarOCrearCategoria(categorias, "Liderazgo y Derecho", "GestiÃ³n comunitaria, empresa y mirada jurÃ­dica internacional");
        Categoria marketing = buscarOCrearCategoria(categorias, "Marketing y AnÃ¡lisis Internacional", "Estrategia comercial, anÃ¡lisis y relaciones internacionales");
        Categoria aviacion = buscarOCrearCategoria(categorias, "AviaciÃ³n y Operaciones de Vuelo", "FormaciÃ³n para pilotaje comercial, seguridad y toma de decisiones");
        Categoria fisioterapia = buscarOCrearCategoria(categorias, "Salud y Fisioterapia", "Bienestar fÃ­sico, readaptaciÃ³n funcional y prevenciÃ³n de lesiones");
        Categoria gestionProyectos = buscarOCrearCategoria(categorias, "GestiÃ³n de Proyectos Global", "PlanificaciÃ³n, liderazgo de equipos y ejecuciÃ³n de proyectos internacionales");

        // Categorias adicionales para ampliar el catalogo y mejorar el filtro principal.
        Categoria historia = buscarOCrearCategoria(categorias, "Historia y Sociedad", "Procesos historicos, memoria colectiva y comprension del presente");
        buscarOCrearCategoria(categorias, "Cine y ActuaciÃ³n", "Actuacion, lenguaje audiovisual y construccion de personajes");
        buscarOCrearCategoria(categorias, "Arquitectura y Urbanismo", "Proyecto, obra y diseÃ±o de espacios habitables");
        buscarOCrearCategoria(categorias, "Cultura Pop y Narrativa", "Universos narrativos, fandom y analisis cultural");

        fusionarCategoriasRelacionadas(
            disenoProgramacion,
            Arrays.asList("DiseÃ±o", "Diseno", "DiseÃ±o Web", "Diseno Web", "ProgramaciÃ³n", "Programacion", "Desarrollo Web", "Frontend", "Backend")
        );

        List<Curso> cursos = new ArrayList<>();
        cursos.add(crearCurso("Grandes preguntas del universo", "Ciencia, pensamiento y las grandes preguntas del universo explicadas con claridad.", "Juan MartÃ­n Maldacena", "/uploads/images/img-027.png", "/uploads/videos/vid-004.mp4", ciencia));
        cursos.add(crearCurso("Humor con mirada crÃ­tica", "Humor, lenguaje y creatividad: cÃ³mo se construye una mirada crÃ­tica desde la risa.", "Yayo Guridi", "/uploads/images/img-010.png", "/uploads/videos/vid-010.mp4", humor));
        cursos.add(crearCurso("Idiomas y compromiso social global", "EducaciÃ³n, idiomas y compromiso social desde la experiencia rotaria internacional.", "Victoria Tejerina Allende", null, null, educacion));
        cursos.add(crearCurso("TecnologÃ­a en la educaciÃ³n superior", "Sistemas, educaciÃ³n superior y el rol de la tecnologÃ­a en la formaciÃ³n acadÃ©mica.", "Fernando Javier Villagra", "/uploads/images/img-019.jpg", "/uploads/videos/vid-009.mp4", tecnologia));
        cursos.add(crearCurso("Escritura para entender el presente", "Escritura, pensamiento y literatura como herramienta para entender el presente.", "Rosa Montero", "/uploads/images/img-003.png", "/uploads/videos/vid-024.mp4", literatura));
        cursos.add(crearCurso("GestiÃ³n hotelera con excelencia", "Experiencia real en hotelerÃ­a premiada, gestiÃ³n de equipos y excelencia en servicio.", "Christine Fox & David Mcghie", "/uploads/images/img-032.jpg", "/uploads/videos/vid-026.mp4", hoteleria));
        cursos.add(crearCurso("Liderazgo, empresa y derecho", "Liderazgo comunitario, empresa y derecho desde una mirada internacional.", "Mel Powell", null, null, liderazgo));
        cursos.add(crearCurso("Marketing aplicado al mundo real", "Marketing, anÃ¡lisis y relaciones internacionales aplicadas al mundo real.", "Francesco Nicola Bute", null, null, marketing));
        cursos.add(crearCurso("Instructor de vuelo: de privado a comercial", "Entrenamiento integral con enfoque operativo: navegaciÃ³n, meteorologÃ­a, CRM y toma de decisiones para pilotos privados y comerciales.", "Alejo Testa", "/uploads/images/img-004.jpg", "/uploads/videos/vid-002.mp4", aviacion));
        cursos.add(crearCurso("El pasado que explica todo", "Descubre procesos historicos que construyen el presente desde una mirada critica y bien documentada.", "Nils Jacobsen", "/uploads/images/img-008.png", "/uploads/videos/vid-006.mp4", historia));
        cursos.add(crearCurso("Fisioterapia funcional para la vida diaria", "Movilidad, prevenciÃ³n de lesiones y ejercicios funcionales para mejorar la calidad de vida.", "Tu mejor amigo - Fisioterapeuta", "/uploads/images/img-014.png", "/uploads/videos/vid-001.mp4", fisioterapia));
        cursos.add(crearCurso("GestiÃ³n de proyectos de inicio a cierre", "MetodologÃ­as, planificaciÃ³n y seguimiento para liderar proyectos con alcance global.", "Juan de Dios", "/uploads/images/img-006.jpeg", "/uploads/videos/vid-014.mp4", gestionProyectos));

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

