package com.parde4.climaapi.repository;

import com.parde4.climaapi.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialRepository extends JpaRepository<Historial, Integer> {

    List<Historial> findByUsuarioIdAndDeletedAtIsNull(Integer usuarioId);
}