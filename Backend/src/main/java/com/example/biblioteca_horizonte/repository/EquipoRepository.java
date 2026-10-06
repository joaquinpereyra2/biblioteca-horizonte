package com.example.biblioteca_horizonte.repository;

import com.example.biblioteca_horizonte.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByActivoTrue();
}