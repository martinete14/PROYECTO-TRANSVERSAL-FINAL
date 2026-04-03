package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.service.CursoService;

@Controller
@RequestMapping("/web/cursos")
public class WebController {

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
            @RequestParam Long categoriaId
    ) {
        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);

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
            @RequestParam Long categoriaId
    ) {
        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);

        Categoria categoria = new Categoria();
        categoria.setId(categoriaId);
        curso.setCategoria(categoria);

        cursoService.actualizarCurso(id, curso);

        return "redirect:/web/cursos";
    }
}