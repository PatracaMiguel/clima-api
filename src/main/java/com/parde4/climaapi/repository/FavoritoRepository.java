package com.parde4.climaapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parde4.climaapi.model.Favorito;


public interface FavoritoRepository extends JpaRepository<Favorito, Long>{
    List<Favorito> findByUsuarioId(Long usuarioId);
}
