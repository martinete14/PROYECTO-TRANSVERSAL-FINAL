/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.service;

import java.util.List;
import java.util.Locale;
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

    @Override
    public List<Curso> obtenerCursosEntidad() {
        return cursoRepository.findAll();
    }

    @Override
    public List<Curso> obtenerCursosPorInstructor(String instructor) {
        if (instructor == null || instructor.isBlank()) {
            return cursoRepository.findAll();
        }
        return cursoRepository.findByInstructorContainingIgnoreCase(instructor.trim());
    }

    @Override
    public void actualizarDestacadoSemana(Long id, boolean destacadoSemana) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        curso.setDestacadoSemana(destacadoSemana);
        cursoRepository.save(curso);
    }

    // mappppper xd
    private CursoDTO mapToDTO(Curso curso) {
        return new CursoDTO(
                curso.getId(),
                curso.getTitulo(),
                construirDescripcionCatalogo(curso),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getVideoUrl(),
                curso.getCategoria().getId(),
            curso.getCategoria().getNombre(),
            curso.isDestacadoSemana()
        );
    }

    private String construirDescripcionCatalogo(Curso curso) {
        String enfoque = obtenerEnfoquePorCategoria(curso);

        return String.format(
                "Este curso incluye %s, ejemplos claros y practica aplicada para avanzar con seguridad.",
                enfoque
        );
    }

    private String obtenerNombreVisible(String instructor) {
        if (instructor == null || instructor.isBlank()) {
            return "Este instructor";
        }

        String[] partes = instructor.trim().split("\\s+");
        if (partes.length >= 2) {
            return partes[0] + " " + partes[1];
        }

        return partes[0];
    }

    private String obtenerEnfoquePorCategoria(Curso curso) {
        if (curso.getCategoria() == null || curso.getCategoria().getNombre() == null) {
            return "contenido actual, criterio profesional";
        }

        String categoria = curso.getCategoria().getNombre().toLowerCase(Locale.ROOT);

        if (categoria.contains("ciencia")) {
            return "analisis riguroso, ideas potentes";
        }
        if (categoria.contains("humor")) {
            return "creatividad verbal, mirada critica";
        }
        if (categoria.contains("idioma") || categoria.contains("educa")) {
            return "comunicacion global, aprendizaje practico";
        }
        if (categoria.contains("tecnolog") || categoria.contains("programaci") || categoria.contains("diseño") || categoria.contains("diseno")) {
            return "vision digital, resolucion practica";
        }
        if (categoria.contains("literatura") || categoria.contains("escritura")) {
            return "lectura profunda, escritura consciente";
        }
        if (categoria.contains("hotel")) {
            return "gestion operativa, servicio excelente";
        }
        if (categoria.contains("gastronom")) {
            return "tecnica culinaria, oficio real";
        }
        if (categoria.contains("liderazgo") || categoria.contains("derecho")) {
            return "criterio estrategico, liderazgo humano";
        }
        if (categoria.contains("marketing")) {
            return "estrategia comercial, analisis util";
        }
        if (categoria.contains("aviaci")) {
            return "disciplina operativa, decisiones seguras";
        }
        if (categoria.contains("fisioterapia") || categoria.contains("salud")) {
            return "movilidad funcional, bienestar corporal";
        }
        if (categoria.contains("proyecto")) {
            return "planificacion ordenada, ejecucion solida";
        }

        return "contenido actual, criterio profesional";
    }
}