package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.PronosticoResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.service.WeatherService;
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
    public WeatherResponseDTO obtenerClima(@PathVariable String ciudad) {
        return weatherService.obtenerClimaPorCiudad(ciudad);
    }

    @GetMapping("/{ciudad}/pronostico")
    public PronosticoResponseDTO obtenerPronostico(@PathVariable String ciudad) {
        return weatherService.obtenerPronosticoPorCiudad(ciudad);
    }
}