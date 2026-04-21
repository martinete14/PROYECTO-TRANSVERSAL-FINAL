/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.Curso;
import com.example.demo.model.Inscripcion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.InscripcionRepository;
import com.example.demo.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceImplTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private InscripcionServiceImpl inscripcionService;

    @Test
    void inscribir_deberiaGuardarCuandoNoExisteRelacion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");

        Curso curso = new Curso();
        curso.setId(2L);
        curso.setTitulo("Spring");

        when(inscripcionRepository.existsByUsuarioIdAndCursoId(1L, 2L)).thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Inscripcion resultado = inscripcionService.inscribir(1L, 2L);

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getUsuario().getNombre());
        assertEquals("Spring", resultado.getCurso().getTitulo());
    }

    @Test
    void inscribir_deberiaFallarSiYaExisteInscripcion() {
        when(inscripcionRepository.existsByUsuarioIdAndCursoId(1L, 2L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inscripcionService.inscribir(1L, 2L));

        assertEquals("El usuario ya está inscrito en este curso", ex.getMessage());
        verify(inscripcionRepository, never()).save(any());
    }
}
