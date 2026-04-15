/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.AuthSessionKeys;
import com.example.demo.model.AuditLog;
import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.CursoContenido;
import com.example.demo.model.CursoDTO;
import com.example.demo.model.CursoFormDTO;
import com.example.demo.model.Inscripcion;
import com.example.demo.model.RolUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoContenidoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.AuditLogService;
import com.example.demo.service.CursoService;
import com.example.demo.service.InscripcionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/web/cursos")
public class WebController {

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_BYTES = 120 * 1024 * 1024; // 120 MB
    private static final long MAX_CLASS_RESOURCE_BYTES = 50 * 1024 * 1024; // 50 MB

    @Value("${app.upload.images-dir:uploads/images}")
    private String imageUploadDir;

    @Value("${app.upload.videos-dir:uploads/videos}")
    private String videoUploadDir;

    @Value("${app.upload.course-content-dir:uploads/course-content}")
    private String courseContentUploadDir;

    private final CursoService cursoService;
    private final CategoriaRepository categoriaRepository;
    private final InscripcionService inscripcionService;
    private final UsuarioRepository usuarioRepository;
    private final CursoContenidoRepository cursoContenidoRepository;
    private final AuditLogService auditLogService;

    public WebController(
        CursoService cursoService,
        CategoriaRepository categoriaRepository,
        InscripcionService inscripcionService,
        UsuarioRepository usuarioRepository,
        CursoContenidoRepository cursoContenidoRepository,
        AuditLogService auditLogService
    ) {
        this.cursoService = cursoService;
        this.categoriaRepository = categoriaRepository;
        this.inscripcionService = inscripcionService;
        this.usuarioRepository = usuarioRepository;
        this.cursoContenidoRepository = cursoContenidoRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String verCursos(@RequestParam(required = false) Long categoriaId, Model model) {
        List<CursoDTO> todosLosCursos = new ArrayList<>(cursoService.obtenerCursos());
        todosLosCursos.forEach(this::sanearTextoCurso);
        todosLosCursos = deduplicarCursosParaCatalogo(todosLosCursos);
        boolean esPortada = categoriaId == null;

        List<CursoDTO> cursosCatalogo;
        List<CursoDTO> cursosDestacados;

        if (esPortada) {
            cursosDestacados = todosLosCursos.stream()
                .filter(CursoDTO::isDestacadoSemana)
                .limit(4)
                .toList();

            List<CursoDTO> cursosNoDestacados = todosLosCursos.stream()
                .filter(curso -> !curso.isDestacadoSemana())
                .collect(Collectors.toCollection(ArrayList::new));

            Collections.shuffle(cursosNoDestacados);
            cursosCatalogo = cursosNoDestacados.stream()
                .limit(8)
                .collect(Collectors.toCollection(ArrayList::new));

            // Si hay menos de 8 no destacados, completar con cursos restantes para mantener 8 tarjetas.
            if (cursosCatalogo.size() < 8) {
                Set<Long> idsActuales = cursosCatalogo.stream().map(CursoDTO::getId).collect(Collectors.toSet());
                for (CursoDTO curso : todosLosCursos) {
                    if (idsActuales.contains(curso.getId())) {
                        continue;
                    }
                    cursosCatalogo.add(curso);
                    idsActuales.add(curso.getId());
                    if (cursosCatalogo.size() >= 8) {
                        break;
                    }
                }
            }
        } else {
            cursosCatalogo = todosLosCursos.stream()
                .filter(curso -> Objects.equals(categoriaId, curso.getCategoriaId()))
                .collect(Collectors.toCollection(ArrayList::new));
            cursosDestacados = List.of();
        }

        model.addAttribute("cursosDestacados", cursosDestacados);
        model.addAttribute("cursos", cursosCatalogo);
        model.addAttribute("categorias", construirCategoriasFiltro(todosLosCursos));
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("esPortada", esPortada);

        return "cursos";
    }

    private List<CursoDTO> deduplicarCursosParaCatalogo(List<CursoDTO> cursos) {
        Map<String, CursoDTO> unicos = new LinkedHashMap<>();

        for (CursoDTO curso : cursos) {
            String key = normalizarTexto(curso.getTitulo()) + "|"
                + normalizarTexto(curso.getInstructor()) + "|"
                + normalizarTexto(curso.getCategoriaNombre());

            unicos.putIfAbsent(key, curso);
        }

        return new ArrayList<>(unicos.values());
    }

    private void sanearTextoCurso(CursoDTO curso) {
        curso.setTitulo(repararMojibake(curso.getTitulo()));
        curso.setDescripcion(repararMojibake(curso.getDescripcion()));
        curso.setInstructor(repararMojibake(curso.getInstructor()));
        curso.setCategoriaNombre(repararMojibake(curso.getCategoriaNombre()));
    }

    private List<CategoriaFiltro> construirCategoriasFiltro(List<CursoDTO> cursos) {
        Map<String, CategoriaFiltro> unicas = new LinkedHashMap<>();

        for (CursoDTO curso : cursos) {
            if (curso.getCategoriaId() == null || !StringUtils.hasText(curso.getCategoriaNombre())) {
                continue;
            }

            String nombreLimpio = repararMojibake(curso.getCategoriaNombre());
            String clave = normalizarTexto(nombreLimpio);
            unicas.putIfAbsent(clave, new CategoriaFiltro(curso.getCategoriaId(), nombreLimpio));
        }

        return unicas.values().stream()
            .sorted(Comparator.comparing(CategoriaFiltro::getNombre, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private String normalizarTexto(String valor) {
        if (!StringUtils.hasText(valor)) {
            return "";
        }

        return valor.toLowerCase(Locale.ROOT)
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .trim();
    }

    private String repararMojibake(String valor) {
        if (!StringUtils.hasText(valor)) {
            return valor;
        }

        if (valor.contains("Ã") || valor.contains("Â")) {
            return new String(valor.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replace("Â", "");
        }

        return valor;
    }

    public static class CategoriaFiltro {
        private final Long id;
        private final String nombre;

        public CategoriaFiltro(Long id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }
    }

    @GetMapping("/admin")
    public String panelAdmin(
        @RequestParam(required = false) String instructor,
        @RequestParam(required = false) String titulo,
        Model model
    ) {
        List<Curso> cursos = cursoService.obtenerCursosFiltrados(instructor, titulo);
        model.addAttribute("cursos", cursos);
        model.addAttribute("instructorFiltro", instructor == null ? "" : instructor);
        model.addAttribute("tituloFiltro", titulo == null ? "" : titulo);
        return "admin-cursos";
    }

    @GetMapping("/admin/logs")
    public String panelAdminLogs(
        @RequestParam(required = false) String actor,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) Integer limit,
        Model model
    ) {
        List<AuditLog> logs = auditLogService.getRecent(actor, accion, limit);
        model.addAttribute("logs", logs);
        model.addAttribute("actorFiltro", actor == null ? "" : actor);
        model.addAttribute("accionFiltro", accion == null ? "" : accion);
        model.addAttribute("limitFiltro", limit == null ? 80 : Math.max(10, Math.min(300, limit)));
        return "admin-logs";
    }

    @PostMapping("/admin/destacado/{id}")
    public String cambiarDestacado(
        @PathVariable Long id,
        @RequestParam boolean destacado,
        @RequestParam(required = false) String instructor,
        @RequestParam(required = false) String titulo,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            cursoService.actualizarDestacadoSemana(id, destacado);
            auditLogService.logFromSession(
                session,
                "COURSE_FEATURE_TOGGLED",
                "Curso ID " + id + " marcado destacado=" + destacado,
                "/web/cursos/admin/destacado/" + id,
                null
            );
            redirectAttributes.addFlashAttribute("successMessage", "Destacado actualizado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        List<String> params = new ArrayList<>();
        if (StringUtils.hasText(instructor)) {
            params.add("instructor=" + instructor.trim());
        }
        if (StringUtils.hasText(titulo)) {
            params.add("titulo=" + titulo.trim());
        }

        if (!params.isEmpty()) {
            return "redirect:/web/cursos/admin?" + String.join("&", params);
        }

        return "redirect:/web/cursos/admin";
    }

    @GetMapping("/instructor")
    public String panelInstructor(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String titulo,
        Model model,
        HttpSession session
    ) {
        RolUsuario rol = obtenerRolSesion(session);
        String nombreFiltro = nombre;

        if (rol == RolUsuario.INSTRUCTOR) {
            nombreFiltro = obtenerUsuarioSesion(session).getNombre();
        }

        List<Curso> cursos = cursoService.obtenerCursosFiltrados(nombreFiltro, titulo);
        model.addAttribute("cursos", cursos);
        model.addAttribute("nombreInstructor", nombreFiltro == null ? "" : nombreFiltro);
        model.addAttribute("tituloFiltro", titulo == null ? "" : titulo);
        return "instructor-cursos";
    }

    @GetMapping("/{id}")
    public String verDetalleCurso(@PathVariable Long id, Model model, HttpSession session) {
        Curso curso = cursoService.obtenerCursoPorId(id);
        RolUsuario rol = obtenerRolSesion(session);

        boolean puedeIrAlAula = rol == RolUsuario.ADMIN;
        if (!puedeIrAlAula && rol == RolUsuario.INSTRUCTOR) {
            Usuario usuario = obtenerUsuarioSesion(session);
            puedeIrAlAula = curso.getInstructor() != null && curso.getInstructor().equalsIgnoreCase(usuario.getNombre());
        }
        if (!puedeIrAlAula && rol == RolUsuario.CLIENTE) {
            Usuario usuario = obtenerUsuarioSesion(session);
            puedeIrAlAula = inscripcionService.estaInscrito(usuario.getId(), id);
        }

        model.addAttribute("curso", curso);
        model.addAttribute("puedeIrAlAula", puedeIrAlAula);
        return "curso-detalle";
    }

    @GetMapping("/adquiridos")
    public String verCursosAdquiridos(Model model, HttpSession session) {
        validarRolCliente(session);
        Usuario usuario = obtenerUsuarioSesion(session);
        List<Inscripcion> inscripciones = inscripcionService.obtenerPorUsuario(usuario.getId());

        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioId", usuario.getId());
        return "cursos-adquiridos";
    }

    @PostMapping("/comprar/{cursoId}")
    public String comprarCurso(@PathVariable Long cursoId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            validarRolCliente(session);
            Usuario usuario = obtenerUsuarioSesion(session);
            inscripcionService.inscribir(usuario.getId(), cursoId);
            redirectAttributes.addFlashAttribute("successMessage", "Curso adquirido correctamente. Ya puedes entrar al aula.");
            return "redirect:/web/cursos/aula/" + cursoId;
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/web/cursos";
        }
    }

    @GetMapping("/aula/{cursoId}")
    public String verAulaCurso(@PathVariable Long cursoId, Model model, HttpSession session) {
        Curso curso = cursoService.obtenerCursoPorId(cursoId);
        Usuario usuario = obtenerUsuarioSesion(session);
        RolUsuario rol = obtenerRolSesion(session);

        if (rol == RolUsuario.CLIENTE && !inscripcionService.estaInscrito(usuario.getId(), cursoId)) {
            throw new RuntimeException("Debes adquirir el curso para entrar al aula.");
        }

        if (rol == RolUsuario.INSTRUCTOR) {
            String instructorCurso = curso.getInstructor() == null ? "" : curso.getInstructor().trim();
            if (!instructorCurso.equalsIgnoreCase(usuario.getNombre().trim())) {
                throw new RuntimeException("Solo el instructor del curso puede acceder a esta aula.");
            }
        }

        List<CursoContenido> contenidos = cursoContenidoRepository.findByCursoIdOrderByOrdenAscIdAsc(cursoId);
        model.addAttribute("curso", curso);
        model.addAttribute("contenidos", contenidos);
        model.addAttribute("puedeGestionarContenido", rol == RolUsuario.ADMIN || rol == RolUsuario.INSTRUCTOR);
        return "aula-curso";
    }

    @GetMapping("/{cursoId}/contenido")
    public String gestionarContenidoCurso(@PathVariable Long cursoId, Model model, HttpSession session) {
        Curso curso = cursoService.obtenerCursoPorId(cursoId);
        validarPermisoGestionCurso(session, curso);

        model.addAttribute("curso", curso);
        model.addAttribute("contenidos", cursoContenidoRepository.findByCursoIdOrderByOrdenAscIdAsc(cursoId));
        return "curso-contenido-admin";
    }

    @PostMapping("/{cursoId}/contenido")
    public String agregarContenidoCurso(
            @PathVariable Long cursoId,
            @RequestParam String titulo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false, defaultValue = "VIDEO") String tipo,
            @RequestParam(required = false) String recursoUrl,
            @RequestParam(required = false) MultipartFile recursoFile,
            @RequestParam(required = false) Integer orden,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Curso curso = cursoService.obtenerCursoPorId(cursoId);
        try {
            validarPermisoGestionCurso(session, curso);
            if (!StringUtils.hasText(titulo) || titulo.trim().length() < 3) {
                throw new RuntimeException("El titulo del contenido debe tener al menos 3 caracteres.");
            }

            String recursoFinal = resolveClassResourceUrl(recursoUrl, recursoFile, cursoId);
            if (!StringUtils.hasText(recursoFinal)) {
                throw new RuntimeException("Debes indicar una URL o subir un archivo de contenido.");
            }

            CursoContenido contenido = new CursoContenido();
            contenido.setCurso(curso);
            contenido.setTitulo(titulo.trim());
            contenido.setDescripcion(StringUtils.hasText(descripcion) ? descripcion.trim() : null);
            contenido.setTipo(StringUtils.hasText(tipo) ? tipo.trim().toUpperCase(Locale.ROOT) : "VIDEO");
            contenido.setRecursoUrl(recursoFinal);
            contenido.setOrden(orden == null ? 99 : orden);

            cursoContenidoRepository.save(contenido);
            auditLogService.logFromSession(session, "CLASS_CONTENT_CREATED", "Contenido agregado al curso ID " + cursoId, "/web/cursos/" + cursoId + "/contenido", null);
            redirectAttributes.addFlashAttribute("successMessage", "Contenido de clase agregado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/web/cursos/" + cursoId + "/contenido";
    }

    @PostMapping("/{cursoId}/contenido/{contenidoId}/eliminar")
    public String eliminarContenidoCurso(
            @PathVariable Long cursoId,
            @PathVariable Long contenidoId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Curso curso = cursoService.obtenerCursoPorId(cursoId);
        try {
            validarPermisoGestionCurso(session, curso);
            CursoContenido contenido = cursoContenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado."));

            if (!Objects.equals(contenido.getCurso().getId(), cursoId)) {
                throw new RuntimeException("El contenido no pertenece al curso seleccionado.");
            }

            tryDeleteUnusedManagedClassResource(contenido.getRecursoUrl());
            cursoContenidoRepository.delete(contenido);

            auditLogService.logFromSession(session, "CLASS_CONTENT_DELETED", "Contenido eliminado del curso ID " + cursoId, "/web/cursos/" + cursoId + "/contenido", null);
            redirectAttributes.addFlashAttribute("successMessage", "Contenido eliminado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/web/cursos/" + cursoId + "/contenido";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        if (!model.containsAttribute("formData")) {
            model.addAttribute("formData", buildFormData("", "", "", "", "", null));
        }
        return "crear-curso";
    }

    @PostMapping("/crear")
    public String crearCurso(
            @ModelAttribute("formData") CursoFormDTO formData,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Usuario usuarioSesion = obtenerUsuarioSesion(session);
            RolUsuario rol = obtenerRolSesion(session);
            String instructorFinal = rol == RolUsuario.INSTRUCTOR ? usuarioSesion.getNombre() : formData.getInstructor();

            formData.setInstructor(instructorFinal);

            Map<String, String> fieldErrors = validarFormularioCurso(
                formData.getTitulo(),
                formData.getDescripcion(),
                instructorFinal,
                formData.getImagenUrl(),
                formData.getVideoUrl(),
                formData.getCategoriaId(),
                formData.getImagenFile(),
                formData.getVideoFile()
            );
            if (!fieldErrors.isEmpty()) {
                model.addAttribute("categorias", categoriaRepository.findAll());
                model.addAttribute("errorMessage", "Revisa los campos marcados e intenta de nuevo.");
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("formData", formData);
                return "crear-curso";
            }

            Curso curso = new Curso();
            curso.setTitulo(formData.getTitulo());
            curso.setDescripcion(formData.getDescripcion());
            curso.setInstructor(instructorFinal);

            validateImageFile(formData.getImagenFile());
            validateVideoFile(formData.getVideoFile());

            String imagenFinal = resolveMediaUrl(formData.getImagenUrl(), formData.getImagenFile(), imageUploadDir, "/uploads/images/");
            String videoFinal = resolveMediaUrl(formData.getVideoUrl(), formData.getVideoFile(), videoUploadDir, "/uploads/videos/");

            curso.setImagenUrl(imagenFinal);
            curso.setVideoUrl(videoFinal);

            Categoria categoria = new Categoria();
            categoria.setId(formData.getCategoriaId());
            curso.setCategoria(categoria);

            cursoService.crearCurso(curso);
            auditLogService.logFromSession(
                session,
                "COURSE_CREATED",
                "Curso creado: " + curso.getTitulo(),
                "/web/cursos/crear",
                null
            );
            redirectAttributes.addFlashAttribute("successMessage", "Curso creado correctamente. Ya puedes verlo en el catalogo.");
            return "redirect:/web/cursos";
        } catch (RuntimeException ex) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            Map<String, String> fieldErrors = mapearErroresDesdeMensaje(ex.getMessage());
            if (!fieldErrors.isEmpty()) {
                model.addAttribute("errorMessage", "Revisa los campos marcados e intenta de nuevo.");
                model.addAttribute("fieldErrors", fieldErrors);
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            formData.setInstructor(resolveInstructorPreview(session, formData.getInstructor()));
            model.addAttribute("formData", formData);
            return "crear-curso";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Curso curso = cursoService.obtenerCursoPorId(id);
            validarPermisoGestionCurso(session, curso);

            String imagenAnterior = curso.getImagenUrl();
            String videoAnterior = curso.getVideoUrl();

            cursoService.eliminarCurso(id);
            tryDeleteUnusedManagedMedia(imagenAnterior, id);
            tryDeleteUnusedManagedMedia(videoAnterior, id);

            auditLogService.logFromSession(
                session,
                "COURSE_DELETED",
                "Curso eliminado ID " + id + " titulo=" + curso.getTitulo(),
                "/web/cursos/eliminar/" + id,
                null
            );

            redirectAttributes.addFlashAttribute("successMessage", "Curso eliminado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/web/cursos";
    }

    // 🔵 EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, HttpSession session) {

        Curso curso = cursoService.obtenerCursoPorId(id);
        validarPermisoGestionCurso(session, curso);

        model.addAttribute("curso", curso);
        model.addAttribute("categorias", categoriaRepository.findAll());

        return "editar-curso";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarCurso(
            @PathVariable Long id,
            @ModelAttribute CursoFormDTO formData,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Curso cursoExistente = null;
        try {
            Usuario usuarioSesion = obtenerUsuarioSesion(session);
            RolUsuario rol = obtenerRolSesion(session);
            String instructorFinal = rol == RolUsuario.INSTRUCTOR ? usuarioSesion.getNombre() : formData.getInstructor();

            Map<String, String> fieldErrors = validarFormularioCurso(
                formData.getTitulo(),
                formData.getDescripcion(),
                instructorFinal,
                formData.getImagenUrl(),
                formData.getVideoUrl(),
                formData.getCategoriaId(),
                formData.getImagenFile(),
                formData.getVideoFile()
            );
            if (!fieldErrors.isEmpty()) {
                cursoExistente = cursoService.obtenerCursoPorId(id);
                validarPermisoGestionCurso(session, cursoExistente);
                model.addAttribute("errorMessage", "Revisa los campos marcados e intenta de nuevo.");
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("categorias", categoriaRepository.findAll());
                model.addAttribute("curso", buildDraftCurso(
                    id,
                    formData.getTitulo(),
                    formData.getDescripcion(),
                    instructorFinal,
                    firstNonBlank(formData.getImagenUrl(), cursoExistente.getImagenUrl()),
                    firstNonBlank(formData.getVideoUrl(), cursoExistente.getVideoUrl()),
                    formData.getCategoriaId(),
                    cursoExistente.getCategoria()
                ));
                return "editar-curso";
            }

            cursoExistente = cursoService.obtenerCursoPorId(id);
            validarPermisoGestionCurso(session, cursoExistente);

            String imagenAnterior = cursoExistente.getImagenUrl();
            String videoAnterior = cursoExistente.getVideoUrl();

            Curso curso = new Curso();
            curso.setTitulo(formData.getTitulo());
            curso.setDescripcion(formData.getDescripcion());
            curso.setInstructor(instructorFinal);

            validateImageFile(formData.getImagenFile());
            validateVideoFile(formData.getVideoFile());

            String imagenFinal = resolveMediaUrl(formData.getImagenUrl(), formData.getImagenFile(), imageUploadDir, "/uploads/images/");
            String videoFinal = resolveMediaUrl(formData.getVideoUrl(), formData.getVideoFile(), videoUploadDir, "/uploads/videos/");

            curso.setImagenUrl(firstNonBlank(imagenFinal, cursoExistente.getImagenUrl()));
            curso.setVideoUrl(firstNonBlank(videoFinal, cursoExistente.getVideoUrl()));

            Categoria categoria = new Categoria();
            categoria.setId(formData.getCategoriaId());
            curso.setCategoria(categoria);

            cursoService.actualizarCurso(id, curso);

            auditLogService.logFromSession(
                session,
                "COURSE_UPDATED",
                "Curso actualizado ID " + id + " titulo=" + curso.getTitulo(),
                "/web/cursos/actualizar/" + id,
                null
            );

            if (!Objects.equals(imagenAnterior, curso.getImagenUrl())) {
                tryDeleteUnusedManagedMedia(imagenAnterior, id);
            }
            if (!Objects.equals(videoAnterior, curso.getVideoUrl())) {
                tryDeleteUnusedManagedMedia(videoAnterior, id);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Curso actualizado correctamente.");
            return "redirect:/web/cursos";
        } catch (RuntimeException ex) {
            if (cursoExistente == null) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/web/cursos";
            }

            Map<String, String> fieldErrors = mapearErroresDesdeMensaje(ex.getMessage());
            if (!fieldErrors.isEmpty()) {
                model.addAttribute("errorMessage", "Revisa los campos marcados e intenta de nuevo.");
                model.addAttribute("fieldErrors", fieldErrors);
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("curso", buildDraftCurso(
                id,
                formData.getTitulo(),
                formData.getDescripcion(),
                resolveInstructorPreview(session, formData.getInstructor()),
                firstNonBlank(formData.getImagenUrl(), cursoExistente.getImagenUrl()),
                firstNonBlank(formData.getVideoUrl(), cursoExistente.getVideoUrl()),
                formData.getCategoriaId(),
                cursoExistente.getCategoria()
            ));
            return "editar-curso";
        }
    }

    private String resolveMediaUrl(String url, MultipartFile file, String uploadDir, String publicPrefix) {
        if (file != null && !file.isEmpty()) {
            return storeFile(file, uploadDir, publicPrefix);
        }
        if (StringUtils.hasText(url)) {
            return url.trim();
        }
        return null;
    }

    private String firstNonBlank(String value, String fallback) {
        if (StringUtils.hasText(value)) {
            return value;
        }
        return fallback;
    }

    private String resolveClassResourceUrl(String url, MultipartFile file, Long cursoId) {
        if (file != null && !file.isEmpty()) {
            return storeClassResourceFile(file, cursoId);
        }
        if (StringUtils.hasText(url)) {
            String clean = url.trim();
            if (!clean.startsWith("http://") && !clean.startsWith("https://") && !clean.startsWith("/uploads/course-content/")) {
                throw new RuntimeException("La URL de contenido debe empezar por https://, http:// o /uploads/course-content/.");
            }
            return clean;
        }
        return null;
    }

    private String storeClassResourceFile(MultipartFile file, Long cursoId) {
        if (file.getSize() > MAX_CLASS_RESOURCE_BYTES) {
            throw new RuntimeException("El recurso de clase supera el limite de 50 MB.");
        }

        try {
            Path uploadPath = Paths.get(courseContentUploadDir).resolve(String.valueOf(cursoId));
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String fileName = "resource-" + UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/course-content/" + cursoId + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo guardar el recurso de clase.", ex);
        }
    }

    private void tryDeleteUnusedManagedClassResource(String resourceUrl) {
        if (!StringUtils.hasText(resourceUrl) || !resourceUrl.startsWith("/uploads/course-content/")) {
            return;
        }

        String relative = resourceUrl.substring("/uploads/course-content/".length());
        Path localPath = Paths.get(courseContentUploadDir).resolve(relative);
        try {
            Files.deleteIfExists(localPath);
        } catch (IOException ignored) {
            // No interrumpir flujo de negocio por limpieza de archivo.
        }
    }

    private CursoFormDTO buildFormData(String titulo, String descripcion, String instructor, String imagenUrl, String videoUrl, Long categoriaId) {
        CursoFormDTO formData = new CursoFormDTO();
        formData.setTitulo(titulo);
        formData.setDescripcion(descripcion);
        formData.setInstructor(instructor);
        formData.setImagenUrl(imagenUrl);
        formData.setVideoUrl(videoUrl);
        formData.setCategoriaId(categoriaId);
        return formData;
    }

    private Curso buildDraftCurso(Long id, String titulo, String descripcion, String instructor, String imagenUrl, String videoUrl, Long categoriaId, Categoria categoriaFallback) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);
        curso.setImagenUrl(imagenUrl);
        curso.setVideoUrl(videoUrl);

        Categoria categoria = new Categoria();
        if (categoriaId != null) {
            categoria.setId(categoriaId);
        } else if (categoriaFallback != null) {
            categoria.setId(categoriaFallback.getId());
            categoria.setNombre(categoriaFallback.getNombre());
        }
        if (categoria.getNombre() == null && categoriaFallback != null && Objects.equals(categoria.getId(), categoriaFallback.getId())) {
            categoria.setNombre(categoriaFallback.getNombre());
        }
        curso.setCategoria(categoria);
        return curso;
    }

    private String resolveInstructorPreview(HttpSession session, String instructor) {
        try {
            return obtenerRolSesion(session) == RolUsuario.INSTRUCTOR
                ? obtenerUsuarioSesion(session).getNombre()
                : instructor;
        } catch (RuntimeException ex) {
            return instructor;
        }
    }

    private Map<String, String> validarFormularioCurso(
        String titulo,
        String descripcion,
        String instructor,
        String imagenUrl,
        String videoUrl,
        Long categoriaId,
        MultipartFile imagenFile,
        MultipartFile videoFile
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        validarTextoCampo(titulo, 8, 120, "titulo", "El titulo", errors);
        validarTextoCampo(descripcion, 30, 1800, "descripcion", "La descripcion", errors);
        validarTextoCampo(instructor, 3, 120, "instructor", "El nombre del profesional", errors);

        if (categoriaId == null) {
            errors.put("categoriaId", "Debes seleccionar una categoria.");
        }

        validarUrlCampo(imagenUrl, true, "imagenUrl", errors);
        validarUrlCampo(videoUrl, false, "videoUrl", errors);

        try {
            validateImageFile(imagenFile);
        } catch (RuntimeException ex) {
            errors.put("imagenFile", ex.getMessage());
        }

        try {
            validateVideoFile(videoFile);
        } catch (RuntimeException ex) {
            errors.put("videoFile", ex.getMessage());
        }

        return errors;
    }

    private void validarTextoCampo(String valor, int min, int max, String key, String label, Map<String, String> errors) {
        String limpio = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (!StringUtils.hasText(limpio)) {
            errors.put(key, label + " es obligatorio.");
            return;
        }
        if (limpio.length() < min) {
            errors.put(key, label + " debe tener al menos " + min + " caracteres.");
            return;
        }
        if (limpio.length() > max) {
            errors.put(key, label + " no puede superar los " + max + " caracteres.");
        }
    }

    private void validarUrlCampo(String url, boolean imagen, String key, Map<String, String> errors) {
        if (!StringUtils.hasText(url)) {
            return;
        }

        String limpia = limpiarQueryString(url).toLowerCase(Locale.ROOT);
        boolean esLocalValida = imagen
            ? limpia.startsWith("/uploads/images/")
            : limpia.startsWith("/uploads/videos/");
        boolean esRemotaValida = limpia.startsWith("http://") || limpia.startsWith("https://");

        if (!esLocalValida && !esRemotaValida) {
            errors.put(key, imagen
                ? "La URL de imagen debe empezar por https://, http:// o /uploads/images/."
                : "La URL de video debe empezar por https://, http:// o /uploads/videos/.");
            return;
        }

        boolean extensionValida = imagen
            ? limpia.endsWith(".png") || limpia.endsWith(".jpg") || limpia.endsWith(".jpeg") || limpia.endsWith(".webp") || limpia.endsWith(".gif")
            : limpia.endsWith(".mp4");

        if (!extensionValida) {
            errors.put(key, imagen
                ? "La imagen debe ser PNG, JPG, JPEG, WEBP o GIF."
                : "El video debe estar en formato MP4.");
        }
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

    private Map<String, String> mapearErroresDesdeMensaje(String mensaje) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (!StringUtils.hasText(mensaje)) {
            return errors;
        }

        String m = mensaje.toLowerCase(Locale.ROOT);
        if (m.contains("titulo")) {
            errors.put("titulo", mensaje);
        } else if (m.contains("descripcion")) {
            errors.put("descripcion", mensaje);
        } else if (m.contains("profesional") || m.contains("instructor")) {
            errors.put("instructor", mensaje);
        } else if (m.contains("categor") || m.contains("categoria")) {
            errors.put("categoriaId", mensaje);
        } else if (m.contains("url de imagen") || m.contains("imagen debe")) {
            errors.put("imagenUrl", mensaje);
        } else if (m.contains("url de video") || m.contains("video debe")) {
            errors.put("videoUrl", mensaje);
        }

        return errors;
    }

    private String storeFile(MultipartFile file, String uploadDir, String publicPrefix) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }

            String fileName = UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return publicPrefix + fileName;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo multimedia", e);
        }
    }

    private void tryDeleteUnusedManagedMedia(String mediaUrl, Long excludedCursoId) {
        if (!StringUtils.hasText(mediaUrl) || !isManagedUploadPath(mediaUrl)) {
            return;
        }

        boolean inUse = cursoService.obtenerCursosEntidad().stream()
            .filter(curso -> !Objects.equals(curso.getId(), excludedCursoId))
            .anyMatch(curso -> Objects.equals(mediaUrl, curso.getImagenUrl()) || Objects.equals(mediaUrl, curso.getVideoUrl()));

        if (inUse) {
            return;
        }

        Path localPath = resolveManagedMediaPath(mediaUrl);
        if (localPath == null) {
            return;
        }

        try {
            Files.deleteIfExists(localPath);
        } catch (IOException ignored) {
            // No interrumpir el flujo CRUD por un fallo de limpieza.
        }
    }

    private boolean isManagedUploadPath(String mediaUrl) {
        return mediaUrl.startsWith("/uploads/images/") || mediaUrl.startsWith("/uploads/videos/");
    }

    private Path resolveManagedMediaPath(String mediaUrl) {
        String fileName = mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1);
        if (!StringUtils.hasText(fileName)) {
            return null;
        }

        if (mediaUrl.startsWith("/uploads/images/")) {
            return Paths.get(imageUploadDir).resolve(fileName);
        }

        if (mediaUrl.startsWith("/uploads/videos/")) {
            return Paths.get(videoUploadDir).resolve(fileName);
        }

        return null;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo de imagen no es valido");
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new RuntimeException("La imagen supera el limite de 5 MB");
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("video/")) {
            throw new RuntimeException("El archivo de video no es valido");
        }
        if (file.getSize() > MAX_VIDEO_BYTES) {
            throw new RuntimeException("El video supera el limite de 120 MB");
        }
    }

    private Usuario obtenerUsuarioSesion(HttpSession session) {
        Object authUserId = session.getAttribute(AuthSessionKeys.AUTH_USER_ID);
        if (!(authUserId instanceof Long userId)) {
            throw new RuntimeException("Sesion invalida. Inicia sesion nuevamente.");
        }

        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("El usuario de la sesion no existe."));
    }

    private RolUsuario obtenerRolSesion(HttpSession session) {
        Object role = session.getAttribute(AuthSessionKeys.AUTH_ROLE);
        return RolUsuario.fromValue(role == null ? null : role.toString());
    }

    private void validarPermisoGestionCurso(HttpSession session, Curso curso) {
        RolUsuario rol = obtenerRolSesion(session);
        if (rol == RolUsuario.ADMIN) {
            return;
        }

        Usuario usuario = obtenerUsuarioSesion(session);
        String nombreInstructor = curso.getInstructor() == null ? "" : curso.getInstructor().trim();

        if (rol == RolUsuario.INSTRUCTOR && nombreInstructor.equalsIgnoreCase(usuario.getNombre().trim())) {
            return;
        }

        throw new RuntimeException("No tienes permisos para gestionar este curso.");
    }

    private void validarRolCliente(HttpSession session) {
        if (obtenerRolSesion(session) != RolUsuario.CLIENTE) {
            throw new RuntimeException("Esta accion esta disponible solo para el rol CLIENTE.");
        }
    }
}