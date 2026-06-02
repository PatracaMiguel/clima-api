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

    public Favorito guardarFavorito(Favorito favorito){
        if (favoritoRepository.existsByUsuarioIdAndCiudadIgnoreCase(
                favorito.getUsuarioId(), favorito.getCiudad())) {
            throw new RuntimeException("La ciudad ya esta guardada en favoritos");
        }

        favorito.setFechaAgregado(LocalDateTime.now());
        
        return favoritoRepository.save(favorito);
    }

    public List<Favorito> listarPorUsuario(Integer usuarioId){
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    public void eliminarFavorito(Integer id, Integer usuarioId){
        favoritoRepository.deleteById(id);
    }
}
