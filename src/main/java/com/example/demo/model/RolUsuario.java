package com.example.demo.model;

public enum RolUsuario {
    ADMIN,
    INSTRUCTOR,
    CLIENTE;

    public static RolUsuario fromValue(String value) {
        if (value == null || value.isBlank()) {
            return CLIENTE;
        }

        try {
            return RolUsuario.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return CLIENTE;
        }
    }
}
