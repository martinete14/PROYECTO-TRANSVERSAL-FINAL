/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceImplTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CursoServiceImpl cursoService;

    @Test
    void crearCurso_deberiaGuardarCuandoCategoriaExiste() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Programacion");

        Curso curso = new Curso();
        curso.setTitulo("Spring Boot profesional");
        curso.setDescripcion("Un curso completo para construir aplicaciones reales con Spring Boot y buenas practicas.");
        curso.setInstructor("Laura Gomez");
        curso.setCategoria(categoria);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cursoRepository.findAll()).thenReturn(java.util.List.of());

        Curso resultado = cursoService.crearCurso(curso);

        assertEquals("Spring Boot profesional", resultado.getTitulo());
        assertEquals(1L, resultado.getCategoria().getId());
        verify(cursoRepository).save(curso);
    }

    @Test
    void crearCurso_deberiaFallarSinCategoria() {
        Curso curso = new Curso();
        curso.setTitulo("Curso valido sin categoria");
        curso.setDescripcion("Descripcion suficientemente larga para validar solo la ausencia de categoria.");
        curso.setInstructor("Laura Gomez");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cursoService.crearCurso(curso));

        assertEquals("La categoría es obligatoria", ex.getMessage());
        verify(cursoRepository, never()).save(any());
    }

    @Test
    void actualizarCurso_deberiaActualizarCampos() {
        Categoria categoriaActualizada = new Categoria();
        categoriaActualizada.setId(2L);
        categoriaActualizada.setNombre("Diseno Web");

        Curso existente = new Curso();
        existente.setId(10L);
        existente.setTitulo("Titulo viejo");
        existente.setDescripcion("Desc vieja");

        Curso actualizado = new Curso();
        actualizado.setTitulo("Titulo nuevo");
        actualizado.setDescripcion("Descripcion nueva suficientemente completa para validar la longitud minima requerida.");
        actualizado.setInstructor("Profe");
        actualizado.setImagenUrl("/uploads/images/a.jpg");
        actualizado.setVideoUrl("/uploads/videos/a.mp4");
        actualizado.setCategoria(categoriaActualizada);

        when(cursoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoriaActualizada));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cursoRepository.findAll()).thenReturn(java.util.List.of(existente));

        Curso resultado = cursoService.actualizarCurso(10L, actualizado);

        assertEquals("Titulo nuevo", resultado.getTitulo());
        assertEquals("Profe", resultado.getInstructor());
        assertEquals("/uploads/videos/a.mp4", resultado.getVideoUrl());
        assertEquals(2L, resultado.getCategoria().getId());
    }

    @Test
    void crearCurso_deberiaFallarCuandoTituloEsDemasiadoCorto() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Curso curso = new Curso();
        curso.setTitulo("Corto");
        curso.setDescripcion("Descripcion suficiente para que falle solo por el titulo demasiado corto.");
        curso.setInstructor("Laura Gomez");
        curso.setCategoria(categoria);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cursoService.crearCurso(curso));

        assertEquals("El titulo debe tener al menos 8 caracteres", ex.getMessage());
        verify(cursoRepository, never()).save(any());
    }

    @Test
    void crearCurso_deberiaFallarCuandoVideoUrlNoEsMp4() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);

        Curso curso = new Curso();
        curso.setTitulo("Curso de validacion avanzada");
        curso.setDescripcion("Descripcion suficiente para que la validacion se centre en el formato del video.");
        curso.setInstructor("Laura Gomez");
        curso.setVideoUrl("https://example.com/video.webm");
        curso.setCategoria(categoria);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cursoService.crearCurso(curso));

        assertEquals("El video debe estar en formato MP4", ex.getMessage());
        verify(cursoRepository, never()).save(any());
    }
}
