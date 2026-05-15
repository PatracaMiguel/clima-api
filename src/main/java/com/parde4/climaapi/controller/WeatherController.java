package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.PronosticoResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.service.WeatherService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clima")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{ciudad}")
    public WeatherResponseDTO obtenerClima(@PathVariable String ciudad, HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de consultar el clima");
        }

        return weatherService.obtenerClimaPorCiudad(ciudad, usuarioId);
    }

    @GetMapping("/{ciudad}/pronostico")
    public PronosticoResponseDTO obtenerPronostico(@PathVariable String ciudad , HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de consultar el clima");
        }
        return weatherService.obtenerPronosticoPorCiudad(ciudad);
    }
}