package com.example.demo.model;

public class CursoDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private Long categoriaId;
    private String categoriaNombre;

    public CursoDTO() {}

    public CursoDTO(Long id, String titulo, String descripcion, Long categoriaId, String categoriaNombre) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.categoriaNombre = categoriaNombre;
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }
}