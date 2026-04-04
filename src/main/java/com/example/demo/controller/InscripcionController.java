/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Inscripcion;
import com.example.demo.service.InscripcionService;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping
    public Inscripcion inscribir(@RequestParam Long usuarioId,
                                @RequestParam Long cursoId) {
        return inscripcionService.inscribir(usuarioId, cursoId);
    }

    @GetMapping("/usuario/{id}")
    public List<Inscripcion> obtenerPorUsuario(@PathVariable Long id) {
        return inscripcionService.obtenerPorUsuario(id);
    }
}