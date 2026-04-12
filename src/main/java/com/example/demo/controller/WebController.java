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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.AuthSessionKeys;
import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.CursoDTO;
import com.example.demo.model.Inscripcion;
import com.example.demo.model.RolUsuario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.CursoService;
import com.example.demo.service.InscripcionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/web/cursos")
public class WebController {

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_BYTES = 120 * 1024 * 1024; // 120 MB

    @Value("${app.upload.images-dir:uploads/images}")
    private String imageUploadDir;

    @Value("${app.upload.videos-dir:uploads/videos}")
    private String videoUploadDir;

    private final CursoService cursoService;
    private final CategoriaRepository categoriaRepository;
    private final InscripcionService inscripcionService;
    private final UsuarioRepository usuarioRepository;

    public WebController(
        CursoService cursoService,
        CategoriaRepository categoriaRepository,
        InscripcionService inscripcionService,
        UsuarioRepository usuarioRepository
    ) {
        this.cursoService = cursoService;
        this.categoriaRepository = categoriaRepository;
        this.inscripcionService = inscripcionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String verCursos(@RequestParam(required = false) Long categoriaId, Model model) {
        List<CursoDTO> todosLosCursos = new ArrayList<>(cursoService.obtenerCursos());
        todosLosCursos.forEach(this::sanearTextoCurso);
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
    public String panelAdmin(@RequestParam(required = false) String instructor, Model model) {
        List<Curso> cursos = cursoService.obtenerCursosPorInstructor(instructor);
        model.addAttribute("cursos", cursos);
        model.addAttribute("instructorFiltro", instructor == null ? "" : instructor);
        return "admin-cursos";
    }

    @PostMapping("/admin/destacado/{id}")
    public String cambiarDestacado(
        @PathVariable Long id,
        @RequestParam boolean destacado,
        @RequestParam(required = false) String instructor,
        RedirectAttributes redirectAttributes
    ) {
        try {
            cursoService.actualizarDestacadoSemana(id, destacado);
            redirectAttributes.addFlashAttribute("successMessage", "Destacado actualizado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        if (StringUtils.hasText(instructor)) {
            return "redirect:/web/cursos/admin?instructor=" + instructor;
        }

        return "redirect:/web/cursos/admin";
    }

    @GetMapping("/instructor")
    public String panelInstructor(@RequestParam(required = false) String nombre, Model model, HttpSession session) {
        RolUsuario rol = obtenerRolSesion(session);
        String nombreFiltro = nombre;

        if (rol == RolUsuario.INSTRUCTOR) {
            nombreFiltro = obtenerUsuarioSesion(session).getNombre();
        }

        List<Curso> cursos = cursoService.obtenerCursosPorInstructor(nombreFiltro);
        model.addAttribute("cursos", cursos);
        model.addAttribute("nombreInstructor", nombreFiltro == null ? "" : nombreFiltro);
        return "instructor-cursos";
    }

    @GetMapping("/{id}")
    public String verDetalleCurso(@PathVariable Long id, Model model) {
        Curso curso = cursoService.obtenerCursoPorId(id);

        model.addAttribute("curso", curso);
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
            redirectAttributes.addFlashAttribute("successMessage", "Curso adquirido correctamente. Ya aparece en Mis cursos.");
            return "redirect:/web/cursos/adquiridos";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/web/cursos";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "crear-curso";
    }

    @PostMapping("/crear")
    public String crearCurso(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String instructor,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(required = false) MultipartFile videoFile,
            @RequestParam Long categoriaId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuarioSesion = obtenerUsuarioSesion(session);
            RolUsuario rol = obtenerRolSesion(session);

            Curso curso = new Curso();
            curso.setTitulo(titulo);
            curso.setDescripcion(descripcion);
            curso.setInstructor(rol == RolUsuario.INSTRUCTOR ? usuarioSesion.getNombre() : instructor);

            validateImageFile(imagenFile);
            validateVideoFile(videoFile);

            String imagenFinal = resolveMediaUrl(imagenUrl, imagenFile, imageUploadDir, "/uploads/images/");
            String videoFinal = resolveMediaUrl(videoUrl, videoFile, videoUploadDir, "/uploads/videos/");

            curso.setImagenUrl(imagenFinal);
            curso.setVideoUrl(videoFinal);

            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            curso.setCategoria(categoria);

            cursoService.crearCurso(curso);
            redirectAttributes.addFlashAttribute("successMessage", "Curso creado correctamente. Ya puedes verlo en el catalogo.");
            return "redirect:/web/cursos";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/web/cursos/nuevo";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Curso curso = cursoService.obtenerCursoPorId(id);
            validarPermisoGestionCurso(session, curso);
            cursoService.eliminarCurso(id);
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
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String instructor,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) String videoUrl,
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(required = false) MultipartFile videoFile,
            @RequestParam Long categoriaId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuarioSesion = obtenerUsuarioSesion(session);
            RolUsuario rol = obtenerRolSesion(session);

            Curso cursoExistente = cursoService.obtenerCursoPorId(id);
            validarPermisoGestionCurso(session, cursoExistente);

            Curso curso = new Curso();
            curso.setTitulo(titulo);
            curso.setDescripcion(descripcion);
            curso.setInstructor(rol == RolUsuario.INSTRUCTOR ? usuarioSesion.getNombre() : instructor);

            validateImageFile(imagenFile);
            validateVideoFile(videoFile);

            String imagenFinal = resolveMediaUrl(imagenUrl, imagenFile, imageUploadDir, "/uploads/images/");
            String videoFinal = resolveMediaUrl(videoUrl, videoFile, videoUploadDir, "/uploads/videos/");

            curso.setImagenUrl(firstNonBlank(imagenFinal, cursoExistente.getImagenUrl()));
            curso.setVideoUrl(firstNonBlank(videoFinal, cursoExistente.getVideoUrl()));

            Categoria categoria = new Categoria();
            categoria.setId(categoriaId);
            curso.setCategoria(categoria);

            cursoService.actualizarCurso(id, curso);
            redirectAttributes.addFlashAttribute("successMessage", "Curso actualizado correctamente.");
            return "redirect:/web/cursos";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/web/cursos/editar/" + id;
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
        Object role = session.getAttribute("AUTH_ROLE");
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