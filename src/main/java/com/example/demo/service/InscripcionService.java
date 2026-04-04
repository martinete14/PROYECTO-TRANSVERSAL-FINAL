/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Inscripcion;

public interface InscripcionService {

    Inscripcion inscribir(Long usuarioId, Long cursoId);

    List<Inscripcion> obtenerPorUsuario(Long usuarioId);
}