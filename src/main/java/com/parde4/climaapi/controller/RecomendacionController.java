package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.RecomendacionResponseDTO;
import com.parde4.climaapi.service.RecomendacionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recomendaciones")
public class RecomendacionController {

    @Autowired
    private RecomendacionService recomendacionService;

    @GetMapping("/{ciudad}")
    public RecomendacionResponseDTO recomendarCiudad(@PathVariable String ciudad, HttpSession session) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new IllegalArgumentException("Debes iniciar sesion para obtener recomendaciones.");
        }

        return recomendacionService.recomendarClimaCiudad(ciudad);
    }
}