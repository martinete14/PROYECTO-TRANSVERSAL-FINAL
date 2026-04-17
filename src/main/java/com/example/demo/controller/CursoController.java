/* MartÃ­n Villagra Tejerina - 1Â°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Curso;
import com.example.demo.model.CursoDTO;
import com.example.demo.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public List<CursoDTO> obtenerCursos() {
        return cursoService.obtenerCursos();
    }

    @GetMapping("/categoria/{id}")
    public List<CursoDTO> obtenerPorCategoria(@PathVariable Long id) {
        return cursoService.obtenerPorCategoria(id);
    }

    @PostMapping
    public Curso crearCurso(@Valid @RequestBody Curso curso) {
        return cursoService.crearCurso(curso);
    }

    @PutMapping("/{id}")
    public Curso actualizarCurso(@PathVariable Long id, @Valid @RequestBody Curso curso) {
        return cursoService.actualizarCurso(id, curso);
    }

    @DeleteMapping("/{id}")
    public void eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
    }
}
