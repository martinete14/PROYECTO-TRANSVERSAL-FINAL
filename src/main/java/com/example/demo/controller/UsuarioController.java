package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CursoDTO;
import com.example.demo.service.CursoService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CursoService cursoService;

    public UsuarioController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // 🔥 GET cursos de un usuario
    @GetMapping("/{id}/cursos")
    public List<CursoDTO> obtenerCursosUsuario(@PathVariable Long id) {
        return cursoService.obtenerCursosPorUsuario(id);
    }
}