/* MartÃ­n Villagra Tejerina - 1Â°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Inscripcion;

public interface InscripcionService {

    Inscripcion inscribir(Long usuarioId, Long cursoId);

    List<Inscripcion> obtenerPorUsuario(Long usuarioId);

    boolean estaInscrito(Long usuarioId, Long cursoId);
}
