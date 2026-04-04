package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.service.CursoService;

@Controller
@RequestMapping("/web/cursos")
public class WebController {

    private static final String IMAGE_UPLOAD_DIR = "src/main/resources/static/uploads/images";
    private static final String VIDEO_UPLOAD_DIR = "src/main/resources/static/uploads/videos";

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
            @RequestParam Long categoriaId
    ) {
        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);

        String imagenFinal = resolveMediaUrl(imagenUrl, imagenFile, IMAGE_UPLOAD_DIR, "/uploads/images/");
        String videoFinal = resolveMediaUrl(videoUrl, videoFile, VIDEO_UPLOAD_DIR, "/uploads/videos/");

        curso.setImagenUrl(imagenFinal);
        curso.setVideoUrl(videoFinal);

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        curso.setCategoria(categoria);

        cursoService.crearCurso(curso);

        return "redirect:/web/cursos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
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
            @RequestParam Long categoriaId
    ) {
        Curso cursoExistente = cursoService.obtenerCursoPorId(id);

        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);

        String imagenFinal = resolveMediaUrl(imagenUrl, imagenFile, IMAGE_UPLOAD_DIR, "/uploads/images/");
        String videoFinal = resolveMediaUrl(videoUrl, videoFile, VIDEO_UPLOAD_DIR, "/uploads/videos/");

        curso.setImagenUrl(firstNonBlank(imagenFinal, cursoExistente.getImagenUrl()));
        curso.setVideoUrl(firstNonBlank(videoFinal, cursoExistente.getVideoUrl()));

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        curso.setCategoria(categoria);

        cursoService.actualizarCurso(id, curso);

        return "redirect:/web/cursos";
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
}