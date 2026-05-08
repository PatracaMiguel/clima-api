package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.ForecastSlotDTO;
import com.parde4.climaapi.dto.PronosticoResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

        when(weatherService.obtenerClimaPorCiudad("Veracruz")).thenReturn(mockResponse);

        // 2. Ejecutar la petición y 3. Validar los resultados
        mockMvc.perform(get("/clima/Veracruz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(25.0))
                .andExpect(jsonPath("$.weather[0].description").value("cielo claro"));
    }

    @Test
    public void obtenerPronostico_DebeRetornarClimaActualYListaDeSlotsEn3Horas() throws Exception {
        WeatherResponseDTO actual = new WeatherResponseDTO();
        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(18.0);
        mainData.setFeels_like(17.0);
        actual.setMain(mainData);
        WeatherResponseDTO.Weather weatherData = new WeatherResponseDTO.Weather();
        weatherData.setMain("Rain");
        weatherData.setDescription("lluvia moderada");
        actual.setWeather(List.of(weatherData));

        ForecastSlotDTO slot = new ForecastSlotDTO();
        slot.setHora("2026-05-04 12:00:00");
        slot.setTemp(19.5);
        slot.setFeels_like(18.0);
        slot.setDescripcion("lluvia ligera");

        PronosticoResponseDTO mockResponse = new PronosticoResponseDTO();
        mockResponse.setActual(actual);
        mockResponse.setPronostico(List.of(slot));

        when(weatherService.obtenerPronosticoPorCiudad("Veracruz")).thenReturn(mockResponse);

        mockMvc.perform(get("/clima/Veracruz/pronostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actual.main.temp").value(18.0))
                .andExpect(jsonPath("$.actual.weather[0].description").value("lluvia moderada"))
                .andExpect(jsonPath("$.pronostico[0].hora").value("2026-05-04 12:00:00"))
                .andExpect(jsonPath("$.pronostico[0].temp").value(19.5))
                .andExpect(jsonPath("$.pronostico[0].descripcion").value("lluvia ligera"));
    }
}