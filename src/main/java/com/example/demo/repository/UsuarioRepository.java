/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByEmailIgnoreCase(String email);
}