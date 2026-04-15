package com.example.demo.model;

import org.springframework.web.multipart.MultipartFile;

public class CursoFormDTO {

    private String titulo;
    private String descripcion;
    private String instructor;
    private String imagenUrl;
    private String videoUrl;
    private MultipartFile imagenFile;
    private MultipartFile videoFile;
    private Long categoriaId;

    public CursoFormDTO() {
    }

    public CursoFormDTO(String titulo, String descripcion, String instructor, String imagenUrl, String videoUrl, Long categoriaId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.instructor = instructor;
        this.imagenUrl = imagenUrl;
        this.videoUrl = videoUrl;
        this.categoriaId = categoriaId;
    }

    public String getTitulo() {
        return titulo;
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

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public MultipartFile getImagenFile() {
        return imagenFile;
    }

    public void setImagenFile(MultipartFile imagenFile) {
        this.imagenFile = imagenFile;
    }

    public MultipartFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}