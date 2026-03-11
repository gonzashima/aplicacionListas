package com.listas.modelo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad JPA que representa una lista de precios.
 * Migrado desde la tabla 'listas' en la DB original.
 */
@Setter
@Getter
@Entity
@Table(name = "listas")
public class Lista {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String proveedor;

    public Lista() {}

    public Lista(Integer id, String nombre, String proveedor) {
        this.id = id;
        this.nombre = nombre;
        this.proveedor = proveedor;
    }

}

