package com.listas.modelo.repository;

import com.listas.modelo.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para productos.
 * Reemplaza las queries manuales de ConectorDB.java
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByListaId(int listaId);

    List<Producto> findByListaIdAndNombreContainingIgnoreCase(int listaId, String nombre);

    void deleteByListaId(int listaId);
}

