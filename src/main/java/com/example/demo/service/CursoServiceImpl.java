/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.CursoDTO;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;

@Service
public class CursoServiceImpl implements CursoService {

    private static final int MIN_TITULO_LENGTH = 8;
    private static final int MAX_TITULO_LENGTH = 120;
    private static final int MIN_DESCRIPCION_LENGTH = 30;
    private static final int MAX_DESCRIPCION_LENGTH = 1800;
    private static final int MIN_INSTRUCTOR_LENGTH = 3;
    private static final int MAX_INSTRUCTOR_LENGTH = 120;

    private final CursoRepository cursoRepository;
    private final CategoriaRepository categoriaRepository;

    public CursoServiceImpl(CursoRepository cursoRepository, CategoriaRepository categoriaRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CursoDTO> obtenerCursos() {
        return cursoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoDTO> obtenerPorCategoria(Long id) {
        return cursoRepository.findByCategoriaId(id)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoDTO> obtenerCursosPorUsuario(Long usuarioId) {
        return cursoRepository.findCursosByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Curso obtenerCursoPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    @Override
    public Curso crearCurso(Curso curso) {

        validarYPrepararCurso(curso, null);

        if (curso.getCategoria() == null || curso.getCategoria().getId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        Long categoriaId = curso.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        curso.setCategoria(categoria);

        return cursoRepository.save(curso);
    }

    @Override
    public Curso actualizarCurso(Long id, Curso cursoActualizado) {

        validarYPrepararCurso(cursoActualizado, id);

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        curso.setTitulo(cursoActualizado.getTitulo());
        curso.setDescripcion(cursoActualizado.getDescripcion());
        curso.setInstructor(cursoActualizado.getInstructor());
        curso.setImagenUrl(cursoActualizado.getImagenUrl());
        curso.setVideoUrl(cursoActualizado.getVideoUrl());

        if (cursoActualizado.getCategoria() == null || cursoActualizado.getCategoria().getId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        Long categoriaId = cursoActualizado.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        curso.setCategoria(categoria);

        return cursoRepository.save(curso);
    }

    @Override
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new RuntimeException("Curso no encontrado");
        }
        cursoRepository.deleteById(id);
    }

    @Override
    public List<Curso> obtenerCursosEntidad() {
        return cursoRepository.findAll();
    }

    @Override
    public List<Curso> obtenerCursosPorInstructor(String instructor) {
        return obtenerCursosFiltrados(instructor, null);
    }

    @Override
    public List<Curso> obtenerCursosFiltrados(String instructor, String titulo) {
        String instructorNormalizado = instructor == null ? "" : instructor.trim().toLowerCase(Locale.ROOT);
        String tituloNormalizado = titulo == null ? "" : titulo.trim().toLowerCase(Locale.ROOT);

        return cursoRepository.findAll().stream()
            .filter(curso -> instructorNormalizado.isBlank() || contieneIgnoreCase(curso.getInstructor(), instructorNormalizado))
            .filter(curso -> tituloNormalizado.isBlank() || contieneIgnoreCase(curso.getTitulo(), tituloNormalizado))
            .collect(Collectors.toList());
    }

    @Override
    public void actualizarDestacadoSemana(Long id, boolean destacadoSemana) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        curso.setDestacadoSemana(destacadoSemana);
        cursoRepository.save(curso);
    }

    // mappppper xd
    private CursoDTO mapToDTO(Curso curso) {
        return new CursoDTO(
                curso.getId(),
                curso.getTitulo(),
                construirDescripcionCatalogo(curso),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getVideoUrl(),
                curso.getCategoria().getId(),
            curso.getCategoria().getNombre(),
            curso.isDestacadoSemana()
        );
    }

    private String construirDescripcionCatalogo(Curso curso) {
        String titulo = curso.getTitulo() == null ? "" : curso.getTitulo().trim().toLowerCase(Locale.ROOT);

        if (titulo.contains("grandes preguntas del universo")) {
            return "Si alguna vez miraste al cielo con curiosidad, aqui vas a encontrar ideas enormes contadas de forma clara y fascinante.";
        }
        if (titulo.contains("humor con mirada crítica") || titulo.contains("humor con mirada critica")) {
            return "Un curso con humor inteligente, observacion afilada y ejemplos que te hacen reir mientras te dejan pensando un buen rato.";
        }
        if (titulo.contains("idiomas y compromiso social global")) {
            return "Ideal para abrir la cabeza: idiomas, mirada internacional y aprendizaje util para moverte mejor en un mundo conectado.";
        }
        if (titulo.contains("tecnología en la educación superior") || titulo.contains("tecnologia en la educacion superior")) {
            return "Una guia directa para entender como la tecnologia cambia aulas, equipos y decisiones en la educacion de hoy.";
        }
        if (titulo.contains("escritura para entender el presente")) {
            return "Lecturas, ideas y ejercicios para mirar el presente con mas profundidad y escribir con una voz mucho mas consciente.";
        }
        if (titulo.contains("gestión hotelera con excelencia") || titulo.contains("gestion hotelera con excelencia")) {
            return "Detras del servicio impecable hay estrategia, ritmo y criterio: aqui se aprende como se sostiene de verdad.";
        }
        if (titulo.contains("liderazgo, empresa y derecho")) {
            return "Empresa, comunidad y vision juridica se cruzan en un recorrido pensado para decidir mejor y liderar con mas criterio.";
        }
        if (titulo.contains("marketing aplicado al mundo real")) {
            return "No es marketing de manual: aqui todo baja al terreno real, con ideas que sirven para vender, analizar y comunicar mejor.";
        }
        if (titulo.contains("instructor de vuelo: de privado a comercial")) {
            return "Desde los primeros vuelos hasta la mentalidad comercial, este recorrido te mete en la logica real de cabina y operacion.";
        }
        if (titulo.contains("el pasado que explica todo")) {
            return "La historia deja de sentirse lejana cuando conecta con el presente: este curso atrapa explicando por que seguimos siendo eso que heredamos.";
        }
        if (titulo.contains("fisioterapia funcional para la vida diaria")) {
            return "Un enfoque claro y util para moverte mejor, prevenir molestias y entender tu cuerpo sin rodeos ni tecnicismos innecesarios.";
        }
        if (titulo.contains("gestión de proyectos de inicio a cierre") || titulo.contains("gestion de proyectos de inicio a cierre")) {
            return "Si quieres ordenar proyectos sin perderte entre metodologias, aqui tienes una base clara para planificar, coordinar y cerrar bien.";
        }

        return construirDescripcionGenerica(curso);
    }

    private String construirDescripcionGenerica(Curso curso) {
        String enfoque = obtenerEnfoquePorCategoria(curso);
        String titulo = curso.getTitulo() == null ? "este curso" : curso.getTitulo().trim();
        int variante = Math.abs((titulo + enfoque).hashCode()) % 3;

        return switch (variante) {
            case 0 -> String.format("%s propone %s con un enfoque claro, actual y facil de llevar a la practica.", titulo, enfoque);
            case 1 -> String.format("Una propuesta pensada para descubrir %s sin perderte en teoria innecesaria.", enfoque);
            default -> String.format("Ideas utiles, mirada actual y %s para que el tema te atrape desde el primer minuto.", enfoque);
        };
    }

    private String obtenerEnfoquePorCategoria(Curso curso) {
        if (curso.getCategoria() == null || curso.getCategoria().getNombre() == null) {
            return "contenido actual, criterio profesional";
        }

        String categoria = curso.getCategoria().getNombre().toLowerCase(Locale.ROOT);

        if (categoria.contains("ciencia")) {
            return "analisis riguroso, ideas potentes";
        }
        if (categoria.contains("humor")) {
            return "creatividad verbal, mirada critica";
        }
        if (categoria.contains("idioma") || categoria.contains("educa")) {
            return "comunicacion global, aprendizaje practico";
        }
        if (categoria.contains("tecnolog") || categoria.contains("programaci") || categoria.contains("diseño") || categoria.contains("diseno")) {
            return "vision digital, resolucion practica";
        }
        if (categoria.contains("literatura") || categoria.contains("escritura")) {
            return "lectura profunda, escritura consciente";
        }
        if (categoria.contains("hotel")) {
            return "gestion operativa, servicio excelente";
        }
        if (categoria.contains("gastronom")) {
            return "tecnica culinaria, oficio real";
        }
        if (categoria.contains("liderazgo") || categoria.contains("derecho")) {
            return "criterio estrategico, liderazgo humano";
        }
        if (categoria.contains("marketing")) {
            return "estrategia comercial, analisis util";
        }
        if (categoria.contains("aviaci")) {
            return "disciplina operativa, decisiones seguras";
        }
        if (categoria.contains("fisioterapia") || categoria.contains("salud")) {
            return "movilidad funcional, bienestar corporal";
        }
        if (categoria.contains("proyecto")) {
            return "planificacion ordenada, ejecucion solida";
        }

        return "contenido actual, criterio profesional";
    }

    private void validarYPrepararCurso(Curso curso, Long cursoIdActual) {
        if (curso == null) {
            throw new RuntimeException("Los datos del curso no son validos");
        }

        String titulo = normalizarCampo(curso.getTitulo());
        String descripcion = normalizarCampo(curso.getDescripcion());
        String instructor = normalizarCampo(curso.getInstructor());
        String imagenUrl = normalizarUrl(curso.getImagenUrl());
        String videoUrl = normalizarUrl(curso.getVideoUrl());

        validarLongitud(titulo, "El titulo es obligatorio", "El titulo debe tener al menos 8 caracteres", MAX_TITULO_LENGTH, "El titulo no puede superar los 120 caracteres", MIN_TITULO_LENGTH);
        validarLongitud(descripcion, "La descripcion es obligatoria", "La descripcion debe tener al menos 30 caracteres", MAX_DESCRIPCION_LENGTH, "La descripcion no puede superar los 1800 caracteres", MIN_DESCRIPCION_LENGTH);
        validarLongitud(instructor, "El nombre del profesional es obligatorio", "El nombre del profesional debe tener al menos 3 caracteres", MAX_INSTRUCTOR_LENGTH, "El nombre del profesional no puede superar los 120 caracteres", MIN_INSTRUCTOR_LENGTH);

        validarUrlMultimedia(imagenUrl, true);
        validarUrlMultimedia(videoUrl, false);

        if (existeTituloDuplicado(titulo, cursoIdActual)) {
            throw new RuntimeException("Ya existe un curso con ese titulo. Usa otro nombre para evitar tarjetas duplicadas.");
        }

        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);
        curso.setImagenUrl(imagenUrl);
        curso.setVideoUrl(videoUrl);
    }

    private void validarLongitud(String valor, String mensajeVacio, String mensajeMin, int maximo, String mensajeMax, int minimo) {
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException(mensajeVacio);
        }
        if (valor.length() < minimo) {
            throw new RuntimeException(mensajeMin);
        }
        if (valor.length() > maximo) {
            throw new RuntimeException(mensajeMax);
        }
    }

    private void validarUrlMultimedia(String mediaUrl, boolean imagen) {
        if (mediaUrl == null) {
            return;
        }

        String limpia = limpiarQueryString(mediaUrl).toLowerCase(Locale.ROOT);
        boolean esLocalValida = imagen
                ? limpia.startsWith("/uploads/images/")
                : limpia.startsWith("/uploads/videos/");
        boolean esRemotaValida = limpia.startsWith("http://") || limpia.startsWith("https://");

        if (!esLocalValida && !esRemotaValida) {
            throw new RuntimeException(imagen
                    ? "La URL de imagen debe empezar por https://, http:// o /uploads/images/"
                    : "La URL de video debe empezar por https://, http:// o /uploads/videos/");
        }

        boolean extensionValida = imagen
                ? limpia.endsWith(".png") || limpia.endsWith(".jpg") || limpia.endsWith(".jpeg") || limpia.endsWith(".webp") || limpia.endsWith(".gif")
                : limpia.endsWith(".mp4");

        if (!extensionValida) {
            throw new RuntimeException(imagen
                    ? "La imagen debe ser PNG, JPG, JPEG, WEBP o GIF"
                    : "El video debe estar en formato MP4");
        }
    }

    private boolean existeTituloDuplicado(String titulo, Long cursoIdActual) {
        String tituloNormalizado = normalizarParaComparacion(titulo);

        return cursoRepository.findAll().stream()
                .filter(curso -> cursoIdActual == null || !cursoIdActual.equals(curso.getId()))
                .map(Curso::getTitulo)
                .map(this::normalizarParaComparacion)
                .anyMatch(tituloNormalizado::equals);
    }

    private String normalizarCampo(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarUrl(String valor) {
        String normalizada = normalizarCampo(valor);
        return normalizada == null || normalizada.isBlank() ? null : normalizada;
    }

    private String limpiarQueryString(String valor) {
        int queryIndex = valor.indexOf('?');
        int hashIndex = valor.indexOf('#');
        int corte = valor.length();

        if (queryIndex >= 0) {
            corte = Math.min(corte, queryIndex);
        }
        if (hashIndex >= 0) {
            corte = Math.min(corte, hashIndex);
        }

        return valor.substring(0, corte);
    }

    private String normalizarParaComparacion(String valor) {
        if (valor == null) {
            return "";
        }

        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return sinAcentos.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private boolean contieneIgnoreCase(String valor, String filtroNormalizado) {
        if (valor == null) {
            return false;
        }
        return valor.toLowerCase(Locale.ROOT).contains(filtroNormalizado);
    }
}