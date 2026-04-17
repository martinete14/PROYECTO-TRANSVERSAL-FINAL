/* MartÃ­n Villagra Tejerina - 1Â°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByEmailIgnoreCase(String email);

	boolean existsByStudentId(String studentId);

	boolean existsByTeacherId(String teacherId);

	boolean existsByAdminId(String adminId);
}
