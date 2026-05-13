package com.parde4.climaapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parde4.climaapi.dto.FavoritoRequestDTO;
import com.parde4.climaapi.dto.FavoritoResponseDTO;
import com.parde4.climaapi.model.Favorito;
import com.parde4.climaapi.service.FavoritoService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping
    public FavoritoResponseDTO guardar(@RequestBody @Valid FavoritoRequestDTO dto, HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de guardar favoritos");
        }

        Favorito favorito = new Favorito();
        favorito.setCiudad(dto.getCiudad());
        favorito.setPais(dto.getPais());
        favorito.setUsuarioId(usuarioId);

        return mapToDTO(favoritoService.guardar(favorito));
    }

    @GetMapping
    public List<FavoritoResponseDTO> listar(HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de ver favoritos");
        }

        return favoritoService.listarPorUsuario(usuarioId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id, HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de eliminar favoritos");
        }

        favoritoService.eliminar(id, usuarioId);
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