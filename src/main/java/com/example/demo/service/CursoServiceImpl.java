/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.model.CursoDTO;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CategoriaRepository categoriaRepository;

    public CursoServiceImpl(CursoRepository cursoRepository, CategoriaRepository categoriaRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<CursoDTO> obtenerCursos() {
        return cursoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoDTO> obtenerPorCategoria(Long id) {
        return cursoRepository.findByCategoriaId(id)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CursoDTO> obtenerCursosPorUsuario(Long usuarioId) {
        return cursoRepository.findCursosByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Curso obtenerCursoPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    @Override
    public Curso crearCurso(Curso curso) {

        if (curso.getCategoria() == null || curso.getCategoria().getId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        Long categoriaId = curso.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        curso.setCategoria(categoria);

        return cursoRepository.save(curso);
    }

    @Override
    public Curso actualizarCurso(Long id, Curso cursoActualizado) {

        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        curso.setTitulo(cursoActualizado.getTitulo());
        curso.setDescripcion(cursoActualizado.getDescripcion());
        curso.setInstructor(cursoActualizado.getInstructor());
        curso.setImagenUrl(cursoActualizado.getImagenUrl());
        curso.setVideoUrl(cursoActualizado.getVideoUrl());

        if (cursoActualizado.getCategoria() == null || cursoActualizado.getCategoria().getId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        Long categoriaId = cursoActualizado.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        curso.setCategoria(categoria);

        return cursoRepository.save(curso);
    }

    @Override
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new RuntimeException("Curso no encontrado");
        }
        cursoRepository.deleteById(id);
    }

    // mapper xd
    private CursoDTO mapToDTO(Curso curso) {
        return new CursoDTO(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
            curso.getInstructor(),
            curso.getImagenUrl(),
            curso.getVideoUrl(),
                curso.getCategoria().getId(),
                curso.getCategoria().getNombre()
        );
    }
}