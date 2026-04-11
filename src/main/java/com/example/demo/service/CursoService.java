/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Curso;
import com.example.demo.model.CursoDTO;

public interface CursoService {

    List<CursoDTO> obtenerCursos();

    List<CursoDTO> obtenerPorCategoria(Long id);

    List<CursoDTO> obtenerCursosPorUsuario(Long usuarioId);

    Curso crearCurso(Curso curso);

    Curso actualizarCurso(Long id, Curso curso);

    void eliminarCurso(Long id);

    // 🔵 NUEVO (PARA EDITAR) mejora completa
    Curso obtenerCursoPorId(Long id);

    List<Curso> obtenerCursosEntidad();

    List<Curso> obtenerCursosPorInstructor(String instructor);

    void actualizarDestacadoSemana(Long id, boolean destacadoSemana);
}