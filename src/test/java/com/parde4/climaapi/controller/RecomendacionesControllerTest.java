package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.WeatherResponseDTO;
import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.service.RecomendacionService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecomendacionController.class)
@Import({GlobalExceptionHandler.class, RecomendacionService.class})
class RecomendacionesControllerTest {

    private static final int USUARIO_ID = 1;
    private static final String SIN_MODIFICADORES = "";
    private static final String TEMP_FRIO_EXTREMO =
            "Hace un frío extremo afuera. Usa ropa térmica seguido de un suéter grueso de lana y un abrigo térmico o parka impermeable. Usar pantalones abrigados, botas con suela antideslizante, gorro, bufanda y guantes impermeables.";
    private static final String TEMP_FRIO_MODERADO =
            "El día está bastante frío. Te recomendamos usar una camiseta de manga larga, un suéter o sudadera, y una chamarra, combínalo con jeans gruesos, botas o tenis cerrados de piel. Usar un gorro ligero y una bufanda.";
    private static final String TEMP_TEMPLADO =
            "El clima está fresco y agradable. Te recomendamos usar prendas fáciles de quitar y poner por si cambia el día. Una playera o camisa combinada con una chaqueta ligera como una chamarra de mezclilla o un blazer. Usa jeans o pantalones casuales y tenis.";
    private static final String TEMP_CALIDO =
            "Es un día cálido. Te recomendamos usar ropa fresca y transpirable de algodón o lino: playeras de manga corta, blusas ligeras, bermudas, shorts o vestidos cómodos. Usar tenis ligeros o sandalias, llevar tus lentes de sol y una gorra, si vas a caminar bajo el sol usa protector solar.";
    private static final String TEMP_CALOR_EXTREMO =
            "Calor extremo, te recomendamos mantenerte fresco con ropa muy holgada y de telas ultraligeras, preferentemente en colores claros para no absorber el calor. Usar shorts, faldas y playeras de tirantes, junto con sandalias abiertas. Es obligatorio usar protector solar, lentes de sol y una gorra.";
    private static final String MODIFICADOR_LLUVIA =
            "Alerta de lluvia: Asegúrate de llevar un paraguas resistente y una chamarra impermeable con capucha. Usa calzado impermeable.";
    private static final String MODIFICADOR_NIEVE =
            "Alerta de nieve: Usa ropa impermeable resistente al agua para que la nieve no te moje al derretirse. Usa botas con suela antideslizante para evitar resbalones en el hielo.";
    private static final String MODIFICADOR_VIENTO =
            "Alerta de viento: Te recomendamos usar una chaqueta rompevientos y evitar faldas o vestidos. Si tienes el cabello largo usa una liga o pinza.";
    private static final String MENSAJE_CIUDAD_NO_ENCONTRADA =
            "No se encontro informacion climatica para la ciudad ingresada , cheque bien el nombre e intente nuevamente.";
    private static final String MENSAJE_SESION_REQUERIDA =
            "Debes iniciar sesion para obtener recomendaciones.";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @Test
    @DisplayName("REC-01 - GET /recomendaciones/{ciudad} con 32C y Clear")
    void recomendar_Clear32_RetornaRecomendacionCalurosa() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Cancun", null))
                .thenReturn(crearClima(32.0, "Clear", "clear sky"));

        assertRecomendacion("Cancun", 32.0, "Clear", "clear sky",
                TEMP_CALOR_EXTREMO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-02 - GET /recomendaciones/{ciudad} con 18C y Rain")
    void recomendar_Rain18_RetornaRecomendacionLluvia() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Londres", null))
                .thenReturn(crearClima(18.0, "Rain", "light rain"));

        assertRecomendacion("Londres", 18.0, "Rain", "light rain",
                TEMP_TEMPLADO, MODIFICADOR_LLUVIA, MODIFICADOR_LLUVIA);
    }

    @Test
    @DisplayName("REC-03 - GET /recomendaciones/{ciudad} con 20C y Thunderstorm")
    void recomendar_Thunderstorm20_RetornaRecomendacionTormenta() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Xalapa", null))
                .thenReturn(crearClima(20.0, "Thunderstorm", "thunderstorm"));

        assertRecomendacion("Xalapa", 20.0, "Thunderstorm", "thunderstorm",
                TEMP_TEMPLADO, MODIFICADOR_LLUVIA, MODIFICADOR_LLUVIA);
    }

    @Test
    @DisplayName("REC-04 - GET /recomendaciones/{ciudad} con 19C y Clouds")
    void recomendar_Clouds19_RetornaRecomendacionNublado() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Puebla", null))
                .thenReturn(crearClima(19.0, "Clouds", "cloudy"));

        assertRecomendacion("Puebla", 19.0, "Clouds", "cloudy",
                TEMP_TEMPLADO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-05 - GET /recomendaciones/{ciudad} con -1C y Snow")
    void recomendar_SnowMenos1_RetornaRecomendacionNieve() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Toluca", null))
                .thenReturn(crearClima(-1.0, "Snow", "snow"));

        assertRecomendacion("Toluca", -1.0, "Snow", "snow",
                TEMP_FRIO_EXTREMO, MODIFICADOR_NIEVE, MODIFICADOR_NIEVE);
    }

    @Test
    @DisplayName("REC-06 - GET /recomendaciones/{ciudad} con 14C y fog")
    void recomendar_Fog14_RetornaRecomendacionNieblaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Orizaba", null))
                .thenReturn(crearClima(14.0, "fog", "fog"));

        assertRecomendacion("Orizaba", 14.0, "fog", "fog",
                TEMP_TEMPLADO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-07 - GET /recomendaciones/{ciudad} con 40C y Clear")
    void recomendar_Clear40_RetornaRecomendacionTemperaturaExtrema() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Mexicali", null))
                .thenReturn(crearClima(40.0, "Clear", "clear sky"));

        assertRecomendacion("Mexicali", 40.0, "Clear", "clear sky",
                TEMP_CALOR_EXTREMO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-08 - GET /recomendaciones/{ciudad} con 35C y Clear")
    void recomendar_Clear35_RetornaRecomendacionMuchoCalor() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Hermosillo", null))
                .thenReturn(crearClima(35.0, "Clear", "clear sky"));

        assertRecomendacion("Hermosillo", 35.0, "Clear", "clear sky",
                TEMP_CALOR_EXTREMO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-09 - GET /recomendaciones/{ciudad} con 30C y Mist")
    void recomendar_Mist30_RetornaRecomendacionBrumaCalor() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Veracruz", null))
                .thenReturn(crearClima(30.0, "Mist", "mist"));

        assertRecomendacion("Veracruz", 30.0, "Mist", "mist",
                TEMP_CALOR_EXTREMO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-10 - GET /recomendaciones/{ciudad} con 10C y Haze")
    void recomendar_Haze10_RetornaRecomendacionBrumaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Perote", null))
                .thenReturn(crearClima(10.0, "Haze", "haze"));

        assertRecomendacion("Perote", 10.0, "Haze", "haze",
                TEMP_FRIO_MODERADO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-11 - GET /recomendaciones/{ciudad} con 24C y Clear")
    void recomendar_Clear24_RetornaRecomendacionCalida() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Merida", null))
                .thenReturn(crearClima(24.0, "Clear", "clear sky"));

        assertRecomendacion("Merida", 24.0, "Clear", "clear sky",
                TEMP_CALIDO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-12 - GET /recomendaciones/{ciudad} con 23C y Clouds")
    void recomendar_Clouds23_RetornaRecomendacionFresca() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Queretaro", null))
                .thenReturn(crearClima(23.0, "Clouds", "cloudy"));

        assertRecomendacion("Queretaro", 23.0, "Clouds", "cloudy",
                TEMP_CALIDO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-13 - GET /recomendaciones/{ciudad} con 5C y Clear")
    void recomendar_Clear5_RetornaRecomendacionFrioExtremo() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Chihuahua", null))
                .thenReturn(crearClima(5.0, "Clear", "clear sky"));

        assertRecomendacion("Chihuahua", 5.0, "Clear", "clear sky",
                TEMP_FRIO_MODERADO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-14 - GET /recomendaciones/{ciudad} con 25C y Drizzle")
    void recomendar_Drizzle25_RetornaRecomendacionLluvia() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Cordoba", null))
                .thenReturn(crearClima(25.0, "Drizzle", "drizzle"));

        assertRecomendacion("Cordoba", 25.0, "Drizzle", "drizzle",
                TEMP_CALIDO, MODIFICADOR_LLUVIA, MODIFICADOR_LLUVIA);
    }

    @Test
    @DisplayName("REC-15 - GET /recomendaciones/{ciudad} con 17C y Mist")
    void recomendar_Mist17_RetornaRecomendacionBrumaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Coatepec", null))
                .thenReturn(crearClima(17.0, "Mist", "mist"));

        assertRecomendacion("Coatepec", 17.0, "Mist", "mist",
                TEMP_TEMPLADO, SIN_MODIFICADORES, SIN_MODIFICADORES);
    }

    @Test
    @DisplayName("REC-16 - GET /recomendaciones/{ciudad} con 0C y Snow")
    void recomendar_Snow0_RetornaRecomendacionNieveFrioExtremo() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Nevado", null))
                .thenReturn(crearClima(0.0, "Snow", "snow"));

        assertRecomendacion("Nevado", 0.0, "Snow", "snow",
                TEMP_FRIO_MODERADO, MODIFICADOR_NIEVE, MODIFICADOR_NIEVE);
    }

    @Test
    @DisplayName("REC-17 - GET /recomendaciones/{ciudad} con viento fuerte")
    void recomendar_Clear18ConViento_RetornaModificadorViento() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("LaVentosa", null))
                .thenReturn(crearClima(18.0, "Clear", "clear sky", 10.0));

        assertRecomendacion("LaVentosa", 18.0, "Clear", "clear sky",
                TEMP_TEMPLADO, MODIFICADOR_VIENTO, MODIFICADOR_VIENTO);
    }

    @Test
    @DisplayName("REC-18 - GET /recomendaciones/{ciudad} con nombre de ciudad inexistente")
    void recomendar_CiudadInexistente_RetornaMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("CiudadInexistente", null))
                .thenThrow(ciudadNoEncontrada());

        mockMvc.perform(get("/recomendaciones/CiudadInexistente").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_CIUDAD_NO_ENCONTRADA));
    }

    @Test
    @DisplayName("REC-19 - GET /recomendaciones/{ciudad} sin iniciar sesion")
    void recomendar_SinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/recomendaciones/Cancun"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_SESION_REQUERIDA));
    }

    private void assertRecomendacion(
            String ciudad,
            double temperatura,
            String clima,
            String descripcion,
            String ropa,
            String accesorios,
            String mensaje) throws Exception {

        mockMvc.perform(get("/recomendaciones/{ciudad}", ciudad).sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value(ciudad))
                .andExpect(jsonPath("$.temperatura").value(temperatura))
                .andExpect(jsonPath("$.clima").value(clima))
                .andExpect(jsonPath("$.descripcion").value(descripcion))
                .andExpect(jsonPath("$.recomendacionRopa").value(ropa))
                .andExpect(jsonPath("$.recomendacionAccesorios").value(accesorios))
                .andExpect(jsonPath("$.mensaje").value(mensaje));
    }

    private WeatherResponseDTO crearClima(double temperatura, String clima, String descripcion) {
        return crearClima(temperatura, clima, descripcion, null);
    }

    private WeatherResponseDTO crearClima(double temperatura, String clima, String descripcion, Double velocidadViento) {
        WeatherResponseDTO response = new WeatherResponseDTO();

        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(temperatura);
        response.setMain(mainData);

        WeatherResponseDTO.Weather weather = new WeatherResponseDTO.Weather();
        weather.setMain(clima);
        weather.setDescription(descripcion);
        response.setWeather(List.of(weather));

        if (velocidadViento != null) {
            WeatherResponseDTO.Wind wind = new WeatherResponseDTO.Wind();
            wind.setSpeed(velocidadViento);
            response.setWind(wind);
        }

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
