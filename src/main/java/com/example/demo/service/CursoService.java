/* MartÃ­n Villagra Tejerina - 1Â°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
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

    // ðŸ”µ NUEVO (PARA EDITAR) mejora completa
    Curso obtenerCursoPorId(Long id);

    List<Curso> obtenerCursosEntidad();

    List<Curso> obtenerCursosPorInstructor(String instructor);

    List<Curso> obtenerCursosFiltrados(String instructor, String titulo);

    void actualizarDestacadoSemana(Long id, boolean destacadoSemana);
}
