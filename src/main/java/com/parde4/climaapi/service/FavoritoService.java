package com.parde4.climaapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parde4.climaapi.model.Favorito;
import com.parde4.climaapi.repository.FavoritoRepository;

@Service
public class FavoritoService {
    @Autowired
    private FavoritoRepository favoritoRepository;

    public Favorito guardar(Favorito favorito){
        favorito.setFechaAgregado(LocalDateTime.now());
        return favoritoRepository.save(favorito);
    }

    public List<Favorito> listarPorUsuario(Long usuarioId){
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    public void eliminar(Long id){
        favoritoRepository.deleteById(id);
    }
}
