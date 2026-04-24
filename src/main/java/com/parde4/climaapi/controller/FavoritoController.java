package com.parde4.climaapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parde4.climaapi.dto.FavoritoRequestDTO;
import com.parde4.climaapi.dto.FavoritoResponseDTO;
import com.parde4.climaapi.model.Favorito;
import com.parde4.climaapi.service.FavoritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping
    public FavoritoResponseDTO guardar(@RequestBody @Valid FavoritoRequestDTO dto) {

        Favorito favorito = new Favorito();
        favorito.setCiudad(dto.getCiudad());
        favorito.setPais(dto.getPais());
        favorito.setUsuarioId(dto.getUsuarioId());

        return mapToDTO(favoritoService.guardar(favorito));
    }

    @GetMapping("/usuario/{id}")
    public List<FavoritoResponseDTO> listar(@PathVariable Long id) {
        return favoritoService.listarPorUsuario(id).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        favoritoService.eliminar(id);
    }

    private FavoritoResponseDTO mapToDTO(Favorito favorito) {

        FavoritoResponseDTO dto = new FavoritoResponseDTO();

        dto.setId(favorito.getId());
        dto.setCiudad(favorito.getCiudad());
        dto.setPais(favorito.getPais());
        dto.setUsuarioId(favorito.getUsuarioId());
        dto.setFechaAgregado(favorito.getFechaAgregado());

        return dto;
    }
}