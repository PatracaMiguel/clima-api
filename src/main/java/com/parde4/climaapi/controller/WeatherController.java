package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clima")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public WeatherResponseDTO obtenerClima(@RequestParam String ciudad) {
        return weatherService.obtenerClimaPorCiudad(ciudad);
    }
}