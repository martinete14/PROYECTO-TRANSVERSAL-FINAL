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

    // 🔵 NUEVO (PARA EDITAR)
    Curso obtenerCursoPorId(Long id);
}