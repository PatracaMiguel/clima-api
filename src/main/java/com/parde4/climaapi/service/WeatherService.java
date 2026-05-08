package com.parde4.climaapi.service;

import com.parde4.climaapi.dto.ForecastApiResponseDTO;
import com.parde4.climaapi.dto.ForecastSlotDTO;
import com.parde4.climaapi.dto.PronosticoResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

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

    public PronosticoResponseDTO obtenerPronosticoPorCiudad(String ciudad) {
        String urlActual = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/weather")
                .queryParam("q", ciudad)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "es")
                .toUriString();

        String urlForecast = UriComponentsBuilder.fromUriString(apiUrl)
                .path("/forecast")
                .queryParam("q", ciudad)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "es")
                .queryParam("cnt", 8)
                .toUriString();

        WeatherResponseDTO actual = restTemplate.getForObject(urlActual, WeatherResponseDTO.class);
        ForecastApiResponseDTO forecastApi = restTemplate.getForObject(urlForecast, ForecastApiResponseDTO.class);

        List<ForecastSlotDTO> slots = forecastApi.getList().stream().map(item -> {
            ForecastSlotDTO slot = new ForecastSlotDTO();
            slot.setHora(item.getDt_txt());
            slot.setTemp(item.getMain().getTemp());
            slot.setFeels_like(item.getMain().getFeels_like());
            slot.setDescripcion(item.getWeather().get(0).getDescription());
            return slot;
        }).collect(Collectors.toList());

        PronosticoResponseDTO response = new PronosticoResponseDTO();
        response.setActual(actual);
        response.setPronostico(slots);
        return response;
    }
}