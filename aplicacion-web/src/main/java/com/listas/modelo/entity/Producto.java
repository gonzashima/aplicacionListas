package com.listas.modelo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad JPA que representa un producto.
 * Migrado desde la clase abstracta Producto del proyecto original.
 * En Spring se usa una sola entidad con un campo 'tipo' en vez de subclases.
 */
@Setter
@Getter
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int costo;

    @Column(nullable = false)
    private int precio;

    @Column(nullable = false)
    private int porcentaje;

    @Column(name = "lista_id", nullable = false)
    private int listaId;

    /**
     * Código de casa del proveedor (ej: 247 para Trebol/Duravit).
     * Se calcula en base al tipo de producto.
     */
    @Transient
    private int codigoCasa;

    public Producto() {}

    public Producto(int codigo, String nombre, int costo, int porcentaje, int listaId) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.costo = costo;
        this.porcentaje = porcentaje;
        this.listaId = listaId;
    }

    // ==================== Lógica de negocio ====================

    /**
     * Calcula el precio de venta basado en el tipo de casa/proveedor.
     * Migrado de las subclases de Producto (ProductoDuravit, ProductoMafersa, etc.)
     */
    public void calcularPrecio(String tipoCasa) {
        int costoParcial = calcularCostoParcial(tipoCasa);
        int parcial = costoParcial + (costoParcial * porcentaje) / 100;
        this.precio = redondearPrecio(parcial);
    }

    /**
     * Calcula el costo parcial según las reglas de cada casa.
     * Cada casa tiene una fórmula diferente para llegar al costo parcial.
     */
    private int calcularCostoParcial(String tipoCasa) {
        if (tipoCasa == null) return costo;

        return switch (tipoCasa.toLowerCase()) {
            case "duravit", "respontech" ->
                // costoParcial = costo (sin modificación)
                costo;
            case "mafersa" -> {
                // -30% + medio IVA (10.5%)
                int menosTreinta = costo - (costo * 30) / 100;
                yield (int) (menosTreinta + (menosTreinta * 10.5) / 100);
            }
            case "lumilagro" -> {
                // -30% + IVA completo (21%)
                int menosTreinta = costo - (costo * 30) / 100;
                yield menosTreinta + (menosTreinta * 21) / 100;
            }
            case "rigolleau" -> {
                // -10% + IVA completo (21%)
                int menosDiez = costo - (costo * 10) / 100;
                yield menosDiez + (menosDiez * 21) / 100;
            }
            case "lema", "rodeca" ->
                // + medio IVA (10.5%)
                (int) (costo + 10.5 * costo / 100);
            case "difplast" -> {
                // -20% + medio IVA (10.5%)
                int menosVeinte = costo - costo * 20 / 100;
                yield (int) (menosVeinte + menosVeinte * 10.5 / 100);
            }
            default -> costo;
        };
    }

    /**
     * Redondea el precio a un precio conveniente para la venta.
     * Migrado de ManejadorPrecios.java
     */
    private int redondearPrecio(int precio) {
        int resultado = precio % 10;
        switch (resultado) {
            case 1, 2, 3, 4 -> precio = precio - resultado;
            case 5, 6, 7, 8, 9 -> precio = precio + (10 - resultado);
            default -> {}
        }
        return precioDeVenta(precio);
    }

    private int precioDeVenta(int precio) {
        if (precio >= 100) {
            int resultado = precio % 100;
            switch (resultado) {
                case 10, 20 -> precio = precio - resultado;
                case 30, 40 -> precio = precio + (50 - resultado);
                default -> {}
            }
        }
        return precio;
    }

    // ==================== Getters y Setters ====================

    @Override
    public String toString() {
        return id + " " + codigo + " " + nombre + " " + costo + " " + precio;
    }
}

