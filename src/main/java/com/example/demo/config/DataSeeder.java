/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Mini Academia */
package com.example.demo.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.Categoria;
import com.example.demo.model.Curso;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.CursoRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;

    public DataSeeder(CategoriaRepository categoriaRepository, CursoRepository cursoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public void run(String... args) {
        if (cursoRepository.count() >= 10) {
            return;
        }

        List<Categoria> categorias = categoriaRepository.findAll();

        Categoria programacion = buscarOCrearCategoria(categorias, "Programacion", "Cursos de desarrollo y arquitectura");
        Categoria frontend = buscarOCrearCategoria(categorias, "Diseno Web", "Cursos orientados a UI, UX y maquetacion");
        Categoria datos = buscarOCrearCategoria(categorias, "Bases de Datos", "Cursos de modelado de datos y SQL");

        List<Curso> cursos = new ArrayList<>();
        cursos.add(crearCurso("Java Spring Boot desde cero", "Construye APIs y apps MVC profesionales.", "Laura Gomez", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97", "https://cdn.coverr.co/videos/coverr-student-typing-1578/1080p.mp4", programacion));
        cursos.add(crearCurso("SQL para proyectos reales", "Consultas avanzadas y modelado relacional.", "Carlos Medina", "https://images.unsplash.com/photo-1544383835-bda2bc66a55d", "https://cdn.coverr.co/videos/coverr-man-working-on-laptop-1574/1080p.mp4", datos));
        cursos.add(crearCurso("Diseno UI moderno", "Principios visuales para interfaces limpias.", "Sofia Perez", "https://images.unsplash.com/photo-1498050108023-c5249f4df085", "https://cdn.coverr.co/videos/coverr-developer-at-work-1577/1080p.mp4", frontend));
        cursos.add(crearCurso("Patrones de arquitectura", "MVC, capas y buenas practicas para escalar.", "Andres Ruiz", "https://images.unsplash.com/photo-1521737604893-d14cc237f11d", "https://cdn.coverr.co/videos/coverr-coder-working-at-night-1575/1080p.mp4", programacion));
        cursos.add(crearCurso("MySQL optimizacion", "Indices, joins y rendimiento de consultas.", "Elena Torres", "https://images.unsplash.com/photo-1515879218367-8466d910aaa4", "https://cdn.coverr.co/videos/coverr-web-developer-working-1576/1080p.mp4", datos));
        cursos.add(crearCurso("Bootstrap 5 avanzado", "Componentes responsive y layouts profesionales.", "Diego Ramos", "https://images.unsplash.com/photo-1461749280684-dccba630e2f6", "https://cdn.coverr.co/videos/coverr-programming-on-screen-1573/1080p.mp4", frontend));
        cursos.add(crearCurso("APIs REST con Spring", "Controladores REST, DTOs y validaciones.", "Natalia Vega", "https://images.unsplash.com/photo-1504384308090-c894fdcc538d", "https://cdn.coverr.co/videos/coverr-a-person-typing-code-1572/1080p.mp4", programacion));
        cursos.add(crearCurso("Modelado ER y relacional", "Pasa de diagrama ER a tablas normalizadas.", "Miguel Ortiz", "https://images.unsplash.com/photo-1555066931-4365d14bab8c", "https://cdn.coverr.co/videos/coverr-programming-code-1569/1080p.mp4", datos));
        cursos.add(crearCurso("SEO tecnico para proyectos web", "Estructura semantica y posicionamiento basico.", "Paula Ibarra", "https://images.unsplash.com/photo-1487058792275-0ad4aaf24ca7", "https://cdn.coverr.co/videos/coverr-working-on-a-laptop-1571/1080p.mp4", frontend));
        cursos.add(crearCurso("Proyecto Full Stack DAM/DAW", "Integra backend, BD y frontend en una app final.", "Martin Flores", "https://images.unsplash.com/photo-1518770660439-4636190af475", "https://cdn.coverr.co/videos/coverr-programmer-working-1570/1080p.mp4", programacion));

        Set<String> titulosActuales = new HashSet<>();
        cursoRepository.findAll().forEach(c -> titulosActuales.add(c.getTitulo().toLowerCase()));

        List<Curso> cursosNuevos = cursos.stream()
                .filter(c -> !titulosActuales.contains(c.getTitulo().toLowerCase()))
                .toList();

        if (!cursosNuevos.isEmpty()) {
            cursoRepository.saveAll(cursosNuevos);
        }
    }

    private Categoria buscarOCrearCategoria(List<Categoria> categorias, String nombre, String descripcion) {
        return categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setNombre(nombre);
                    nueva.setDescripcion(descripcion);
                    return categoriaRepository.save(nueva);
                });
    }

    private Curso crearCurso(String titulo, String descripcion, String instructor, String imagenUrl, String videoUrl, Categoria categoria) {
        Curso curso = new Curso();
        curso.setTitulo(titulo);
        curso.setDescripcion(descripcion);
        curso.setInstructor(instructor);
        curso.setImagenUrl(imagenUrl);
        curso.setVideoUrl(videoUrl);
        curso.setCategoria(categoria);
        return curso;
    }
}
