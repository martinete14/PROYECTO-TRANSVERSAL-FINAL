/* Martín Villagra Tejerina - 1°DAW Ilerna 2026 - Proyecto Transversal - Lothar Courses */
package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "El nombre del profesional es obligatorio")
    private String instructor;

    private String imagenUrl;

    private String videoUrl;

    private boolean destacadoSemana;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties("cursos")
    private Categoria categoria;

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isDestacadoSemana() {
        return destacadoSemana;
    }

    public void setDestacadoSemana(boolean destacadoSemana) {
        this.destacadoSemana = destacadoSemana;
    }
}
