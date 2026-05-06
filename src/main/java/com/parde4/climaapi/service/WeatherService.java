package com.parde4.climaapi.service;

import com.parde4.climaapi.dto.WeatherResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherService {

    @Value("${openweathermap.api-url}")
    private String apiUrl;

    @Value("${openweathermap.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Autowired
    private HistorialService historialService;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherResponseDTO obtenerClimaPorCiudad(String ciudad) {
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/weather")
                .queryParam("q", ciudad)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "es")
                .toUriString();

        WeatherResponseDTO response = restTemplate.getForObject(url, WeatherResponseDTO.class);

        if (response != null && response.getMain() != null) {
            historialService.guardar(ciudad, response.getMain().getTemp());
        }

        return response;
    }
}