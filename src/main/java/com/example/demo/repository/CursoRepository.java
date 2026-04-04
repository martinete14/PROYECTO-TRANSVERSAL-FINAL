/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByCategoriaId(Long categoriaId);

    // 🔥 NUEVO
    @Query("SELECT i.curso FROM Inscripcion i WHERE i.usuario.id = :usuarioId")
    List<Curso> findCursosByUsuarioId(Long usuarioId);
}