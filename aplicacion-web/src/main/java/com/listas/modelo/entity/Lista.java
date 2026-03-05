package com.listas.modelo.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa una lista de precios.
 * Migrado desde la tabla 'listas' en la DB original.
 */
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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
}

