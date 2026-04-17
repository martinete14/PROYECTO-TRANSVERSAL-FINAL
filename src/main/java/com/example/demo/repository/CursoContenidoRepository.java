package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CursoContenido;

public interface CursoContenidoRepository extends JpaRepository<CursoContenido, Long> {

    List<CursoContenido> findByCursoIdOrderByOrdenAscIdAsc(Long cursoId);
}
