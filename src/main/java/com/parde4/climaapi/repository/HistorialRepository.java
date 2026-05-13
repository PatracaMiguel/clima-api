package com.parde4.climaapi.repository;

import com.parde4.climaapi.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<Historial, Integer> {
    java.util.List<Historial> findByUsuarioId(Integer usuarioId);
}
