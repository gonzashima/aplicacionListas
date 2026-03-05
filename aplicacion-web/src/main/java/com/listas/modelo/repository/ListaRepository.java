package com.listas.modelo.repository;

import com.listas.modelo.entity.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para listas.
 */
@Repository
public interface ListaRepository extends JpaRepository<Lista, Integer> {
}

