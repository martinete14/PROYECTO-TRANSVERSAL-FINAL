/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Curso;
import com.example.demo.model.Inscripcion;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CursoRepository;
import com.example.demo.repository.InscripcionRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    public InscripcionServiceImpl(InscripcionRepository inscripcionRepository,
                                 UsuarioRepository usuarioRepository,
                                 CursoRepository cursoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public Inscripcion inscribir(Long usuarioId, Long cursoId) {

        if (inscripcionRepository.existsByUsuarioIdAndCursoId(usuarioId, cursoId)) {
            throw new RuntimeException("El usuario ya está inscrito en este curso");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setCurso(curso);

        return inscripcionRepository.save(inscripcion);
    }

    @Override
    public List<Inscripcion> obtenerPorUsuario(Long usuarioId) {
        return inscripcionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public boolean estaInscrito(Long usuarioId, Long cursoId) {
        return inscripcionRepository.existsByUsuarioIdAndCursoId(usuarioId, cursoId);
    }
}
