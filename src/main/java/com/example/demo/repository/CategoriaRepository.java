/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
