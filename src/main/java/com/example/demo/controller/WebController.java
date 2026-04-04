/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.service.CursoService;

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

    public WebController(CursoService cursoService, CategoriaRepository categoriaRepository) {
        this.cursoService = cursoService;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String verCursos(Model model) {
        model.addAttribute("cursos", cursoService.obtenerCursos());
        return "cursos";
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
            RedirectAttributes redirectAttributes
    ) {
        try {
            Curso curso = new Curso();
            curso.setTitulo(titulo);
            curso.setDescripcion(descripcion);
            curso.setInstructor(instructor);

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
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.eliminarCurso(id);
            redirectAttributes.addFlashAttribute("successMessage", "Curso eliminado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/web/cursos";
    }

    // 🔵 EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {

        Curso curso = cursoService.obtenerCursoPorId(id);

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
            RedirectAttributes redirectAttributes
    ) {
        try {
            Curso cursoExistente = cursoService.obtenerCursoPorId(id);

            Curso curso = new Curso();
            curso.setTitulo(titulo);
            curso.setDescripcion(descripcion);
            curso.setInstructor(instructor);

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
}