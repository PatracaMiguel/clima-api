package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // <-- Etiqueta moderna
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private WeatherService weatherService;

    @Test
    public void obtenerClima_DebeRetornarEstatusOkYDatosDelClima() throws Exception {
        WeatherResponseDTO mockResponse = new WeatherResponseDTO();
        
        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(25.0);
        mainData.setFeels_like(26.5);
        mockResponse.setMain(mainData);

        WeatherResponseDTO.Weather weatherData = new WeatherResponseDTO.Weather();
        weatherData.setMain("Clear");
        weatherData.setDescription("cielo claro");
        mockResponse.setWeather(List.of(weatherData));

        // Le decimos al servicio falso cómo debe responder
        when(weatherService.obtenerClimaPorCiudad("Veracruz")).thenReturn(mockResponse);

        // 2. Ejecutar la petición y 3. Validar los resultados
        mockMvc.perform(get("/api/clima")
                .param("ciudad", "Veracruz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(25.0))
                .andExpect(jsonPath("$.weather[0].description").value("cielo claro"));
    }
}