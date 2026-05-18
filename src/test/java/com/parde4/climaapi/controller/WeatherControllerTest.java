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

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(WeatherController.class)
@Import(GlobalExceptionHandler.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    private WeatherResponseDTO crearClimaMock() {
        WeatherResponseDTO response = new WeatherResponseDTO();

        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(25.0);
        mainData.setFeels_like(26.5);
        response.setMain(mainData);

        WeatherResponseDTO.Weather weatherData = new WeatherResponseDTO.Weather();
        weatherData.setMain("Clear");
        weatherData.setDescription("cielo claro");
        response.setWeather(List.of(weatherData));

        return response;
    }

    private PronosticoResponseDTO crearPronosticoMock() {
        WeatherResponseDTO actual = crearClimaMock();

        ForecastSlotDTO slot = new ForecastSlotDTO();
        slot.setHora("2026-05-04 12:00:00");
        slot.setTemp(27.0);
        slot.setFeels_like(28.0);
        slot.setDescripcion("nubes dispersas");

        PronosticoResponseDTO response = new PronosticoResponseDTO();
        response.setActual(actual);
        response.setPronostico(List.of(slot));

        return response;
    }

    private HttpClientErrorException.NotFound ciudadNoEncontradaException() {
    return (HttpClientErrorException.NotFound) HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            HttpHeaders.EMPTY,
            null,
            null
    );
    }

    @Test
    @DisplayName("CLIMA-01 - Ciudad válida con usuario autenticado")
    void clima01CiudadValidaConUsuarioAutenticadoDebeRetornarClimaYGuardarHistorial() throws Exception {
        WeatherResponseDTO response = crearClimaMock();

        when(weatherService.obtenerClimaPorCiudad("Veracruz", 1))
                .thenReturn(response);

        mockMvc.perform(get("/clima/Veracruz")
                        .sessionAttr("usuarioId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(25.0))
                .andExpect(jsonPath("$.main.feels_like").value(26.5))
                .andExpect(jsonPath("$.weather[0].main").value("Clear"))
                .andExpect(jsonPath("$.weather[0].description").value("cielo claro"));

        verify(weatherService).obtenerClimaPorCiudad("Veracruz", 1);
    }

    @Test
    @DisplayName("CLIMA-02 - Ciudad válida sin iniciar sesión")
    void clima02CiudadValidaSinIniciarSesionDebeRetornarMensajeDeInicioSesion() throws Exception {
        mockMvc.perform(get("/clima/Veracruz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("Debe iniciar sesión antes de consultar el clima"));

        verify(weatherService, never()).obtenerClimaPorCiudad("Veracruz", 1);
    }

    @Test
    @DisplayName("CLIMA-03 - Ciudad inexistente en consulta de clima")
    void clima03CiudadInexistenteDebeRetornarMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("CiudadInexistente", 1))
                .thenThrow(ciudadNoEncontradaException());

        mockMvc.perform(get("/clima/CiudadInexistente")
                        .sessionAttr("usuarioId", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Ciudad no encontrada"))
                .andExpect(jsonPath("$.mensaje").value(
                        "No se encontró información climática para la ciudad ingresada , cheque bien el nombre e intente nuevamente."
                ));
    }

    @Test
    @DisplayName("CLIMA-04 - Nombre de ciudad vacío")
    void clima04NombreCiudadVacioDebeRetornarErrorGeneral() throws Exception {
        when(weatherService.obtenerClimaPorCiudad(anyString(), eq(1)))
                .thenThrow(new RuntimeException("Ocurrió un problema en el sistema , intentelo más tarde."));

        mockMvc.perform(get("/clima/%20")
                        .sessionAttr("usuarioId", 1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value(
                        "Ocurrió un problema en el sistema , intentelo más tarde."
                ));
    }

    @Test
    @DisplayName("CLIMA-05 - Ciudad válida con usuario autenticado y pronóstico")
    void clima05CiudadValidaConUsuarioAutenticadoDebeRetornarClimaYPronostico() throws Exception {
        PronosticoResponseDTO response = crearPronosticoMock();

        when(weatherService.obtenerPronosticoPorCiudad("Veracruz", 1))
                .thenReturn(response);

        mockMvc.perform(get("/clima/Veracruz/pronostico")
                        .sessionAttr("usuarioId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actual.main.temp").value(25.0))
                .andExpect(jsonPath("$.actual.main.feels_like").value(26.5))
                .andExpect(jsonPath("$.actual.weather[0].description").value("cielo claro"))
                .andExpect(jsonPath("$.pronostico[0].hora").value("2026-05-04 12:00:00"))
                .andExpect(jsonPath("$.pronostico[0].temp").value(27.0))
                .andExpect(jsonPath("$.pronostico[0].feels_like").value(28.0))
                .andExpect(jsonPath("$.pronostico[0].descripcion").value("nubes dispersas"));

        verify(weatherService).obtenerPronosticoPorCiudad("Veracruz", 1);
    }

    @Test
    @DisplayName("CLIMA-06 - Pronóstico sin iniciar sesión")
    void clima06PronosticoSinInicioDeSesionDebeRetornarMensajeDeInicioSesion() throws Exception {
        mockMvc.perform(get("/clima/Veracruz/pronostico"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("Debe iniciar sesión antes de consultar el clima"));

        verify(weatherService, never()).obtenerPronosticoPorCiudad("Veracruz", 1);
    }

    @Test
    @DisplayName("CLIMA-07 - Ciudad inexistente en consulta de pronóstico")
    void clima07PronosticoConCiudadInexistenteDebeRetornarMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerPronosticoPorCiudad("CiudadInexistente", 1))
                .thenThrow(ciudadNoEncontradaException());

        mockMvc.perform(get("/clima/CiudadInexistente/pronostico")
                        .sessionAttr("usuarioId", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Ciudad no encontrada"))
                .andExpect(jsonPath("$.mensaje").value(
                        "No se encontró información climática para la ciudad ingresada , cheque bien el nombre e intente nuevamente."
                ));
    }

    @Test
    @DisplayName("CLIMA-08 - Pronóstico sin iniciar sesión")
    void clima08PronosticoSinIniciarSesionDebeRetornarMensajeDeInicioSesion() throws Exception {
        mockMvc.perform(get("/clima/Coatzacoalcos/pronostico"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("Debe iniciar sesión antes de consultar el clima"));

        verify(weatherService, never()).obtenerPronosticoPorCiudad("Coatzacoalcos", 1);
    }
}