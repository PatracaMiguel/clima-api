package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.ForecastSlotDTO;
import com.parde4.climaapi.dto.PronosticoResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.service.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@Import(GlobalExceptionHandler.class)
class WeatherControllerTest {

    private static final int USUARIO_ID = 1;
    private static final String MENSAJE_SESION_REQUERIDA = "Debe iniciar sesion antes de consultar el clima";
    private static final String MENSAJE_CIUDAD_NO_ENCONTRADA =
            "No se encontro informacion climatica para la ciudad ingresada , cheque bien el nombre e intente nuevamente.";
    private static final String MENSAJE_ERROR_SISTEMA =
            "Ocurrio un problema en el sistema , intentelo mas tarde.";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    void obtenerClima_CiudadValidaConUsuarioAutenticado_RetornaClimaYGuardaHistorial() throws Exception {
        WeatherResponseDTO mockResponse = crearClima(25.0, 26.5, "Clear", "cielo claro");

        when(weatherService.obtenerClimaPorCiudad("Veracruz", USUARIO_ID)).thenReturn(mockResponse);

        mockMvc.perform(get("/clima/Veracruz").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(25.0))
                .andExpect(jsonPath("$.main.feels_like").value(26.5))
                .andExpect(jsonPath("$.weather[0].main").value("Clear"))
                .andExpect(jsonPath("$.weather[0].description").value("cielo claro"));

        verify(weatherService).obtenerClimaPorCiudad("Veracruz", USUARIO_ID);
    }

    @Test
    void obtenerClima_CiudadValidaSinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/clima/Veracruz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_SESION_REQUERIDA));
    }

    @Test
    @DisplayName("CLIMA-03 - GET /clima/{ciudad} con nombre de ciudad inexistente")
    void obtenerClima_CiudadInexistente_RetornaMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("CiudadInexistente", USUARIO_ID))
                .thenThrow(ciudadNoEncontrada());

        mockMvc.perform(get("/clima/CiudadInexistente").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_CIUDAD_NO_ENCONTRADA));
    }

    @Test
    @DisplayName("CLIMA-04 - GET /clima/{ciudad} con nombre de ciudad vacia")
    void obtenerClima_CiudadVacia_RetornaMensajeDeErrorDelSistema() throws Exception {
        mockMvc.perform(get(URI.create("/clima/%20")).sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_ERROR_SISTEMA));
    }

    @Test
    void obtenerPronostico_CiudadValidaConUsuarioAutenticado_RetornaClimaYPronostico() throws Exception {
        PronosticoResponseDTO mockResponse = crearPronostico();

        when(weatherService.obtenerPronosticoPorCiudad("Veracruz")).thenReturn(mockResponse);

        mockMvc.perform(get("/clima/Veracruz/pronostico").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actual.main.temp").value(18.0))
                .andExpect(jsonPath("$.actual.weather[0].description").value("lluvia moderada"))
                .andExpect(jsonPath("$.pronostico[0].hora").value("2026-05-04 12:00:00"))
                .andExpect(jsonPath("$.pronostico[0].temp").value(19.5))
                .andExpect(jsonPath("$.pronostico[0].descripcion").value("lluvia ligera"));
    }

    @Test
    void obtenerPronostico_CiudadValidaSinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/clima/Veracruz/pronostico"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_SESION_REQUERIDA));
    }

    @Test
    void obtenerPronostico_CiudadInexistente_RetornaMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerPronosticoPorCiudad("CiudadInexistente"))
                .thenThrow(ciudadNoEncontrada());

        mockMvc.perform(get("/clima/CiudadInexistente/pronostico").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_CIUDAD_NO_ENCONTRADA));
    }

    @Test
    void obtenerPronostico_SinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/clima/Xalapa/pronostico"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_SESION_REQUERIDA));
    }

    private WeatherResponseDTO crearClima(double temp, double feelsLike, String main, String description) {
        WeatherResponseDTO response = new WeatherResponseDTO();

        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(temp);
        mainData.setFeels_like(feelsLike);
        response.setMain(mainData);

        WeatherResponseDTO.Weather weatherData = new WeatherResponseDTO.Weather();
        weatherData.setMain(main);
        weatherData.setDescription(description);
        response.setWeather(List.of(weatherData));

        return response;
    }

    private PronosticoResponseDTO crearPronostico() {
        ForecastSlotDTO slot = new ForecastSlotDTO();
        slot.setHora("2026-05-04 12:00:00");
        slot.setTemp(19.5);
        slot.setFeels_like(18.0);
        slot.setDescripcion("lluvia ligera");

        PronosticoResponseDTO response = new PronosticoResponseDTO();
        response.setActual(crearClima(18.0, 17.0, "Rain", "lluvia moderada"));
        response.setPronostico(List.of(slot));
        return response;
    }

    private HttpClientErrorException ciudadNoEncontrada() {
        return HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8);
    }
}
