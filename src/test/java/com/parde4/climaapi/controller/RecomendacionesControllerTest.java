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
    private static final String ROPA_CLEAR =
            "Usa ropa fresca, ligera y de colores claros; una playera de algodon o lino y pantalon ligero funcionan bien.";
    private static final String ACCESORIOS_CLEAR =
            "Lleva lentes de sol, gorra o sombrero, protector solar y una botella de agua.";
    private static final String MENSAJE_CLEAR =
            "El cielo esta despejado; aprovecha el dia, pero protege tu piel y mantente hidratado.";
    private static final String ROPA_RAIN =
            "Usa impermeable o chamarra resistente al agua, pantalon que seque rapido y botas de lluvia o zapatos cerrados con suela antideslizante.";
    private static final String ACCESORIOS_RAIN =
            "Lleva sombrilla, mochila impermeable, una bolsa para proteger celular o documentos y una muda ligera si vas a estar mucho tiempo fuera.";
    private static final String MENSAJE_RAIN =
            "Hay lluvia; sal con tiempo, evita zonas encharcadas y cuida los cambios de temperatura.";
    private static final String ROPA_TORMENTA =
            "Usa impermeable, ropa comoda de secado rapido y calzado cerrado con buena suela.";
    private static final String ACCESORIOS_TORMENTA =
            "Lleva sombrilla resistente, mochila impermeable, linterna pequena y evita cargar objetos metalicos expuestos.";
    private static final String MENSAJE_TORMENTA =
            "Hay tormenta; si puedes, espera a que baje la intensidad y evita resguardarte debajo de arboles.";
    private static final String ROPA_CLOUDS =
            "Usa ropa comoda en capas: playera ligera y una chamarra delgada por si baja la temperatura.";
    private static final String ACCESORIOS_CLOUDS =
            "Lleva lentes de sol si hay resolana y una sombrilla compacta si las nubes se ven densas.";
    private static final String MENSAJE_CLOUDS =
            "El clima esta nublado; conviene salir preparado por si cambia durante el dia.";
    private static final String ROPA_SNOW =
            "Usa ropa termica, sueter grueso, chamarra abrigadora, pantalon resistente al frio y botas.";
    private static final String ACCESORIOS_SNOW =
            "Lleva guantes, gorro, bufanda, calcetines termicos y protector labial.";
    private static final String MENSAJE_SNOW =
            "Hay nieve; abrigate bien y camina con cuidado en superficies resbalosas.";
    private static final String ROPA_BRUMA =
            "Usa ropa comoda y una chamarra ligera si hay humedad o baja visibilidad.";
    private static final String ACCESORIOS_BRUMA =
            "Lleva cubrebocas si hay bruma, luces o reflejantes si caminas, y maneja con precaucion.";
    private static final String MENSAJE_BRUMA =
            "Hay niebla o bruma; reduce la velocidad, manten distancia y evita zonas con poca visibilidad.";
    private static final String TEMP_CALUROSO =
            "El dia esta caluroso: usa ropa ligera, colores claros, lentes de sol y toma agua durante el dia.";
    private static final String TEMP_FRESCO =
            "El clima esta fresco: usa ropa comoda y lleva una chamarra ligera si estaras fuera por la tarde o noche.";
    private static final String TEMP_BAJO_CERO =
            "La temperatura esta bajo cero: usa varias capas de ropa, abrigo grueso, guantes, gorro y bufanda.";
    private static final String TEMP_FRIO =
            "Hace frio: lleva sueter o chamarra, pantalon largo y calzado cerrado para evitar cambios bruscos de temperatura.";
    private static final String TEMP_EXTREMA =
            "La temperatura es extremadamente alta: evita el sol directo, usa ropa muy ligera y transpirable, toma agua constantemente y busca sombra o lugares ventilados.";
    private static final String TEMP_MUCHO_CALOR =
            "Hace mucho calor: usa ropa suelta y fresca, protector solar, lentes de sol, gorra y evita actividad pesada al aire libre.";
    private static final String TEMP_CALIDO =
            "La temperatura es calida y agradable: usa ropa ligera, pero no olvides protector solar si estaras al aire libre.";
    private static final String TEMP_FRIO_EXTREMO =
            "Hace frio intenso: usa chamarra gruesa, sueter, pantalon largo, calcetines abrigadores y calzado cerrado.";
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
                ROPA_CLEAR, ACCESORIOS_CLEAR, MENSAJE_CLEAR + " " + TEMP_CALUROSO);
    }

    @Test
    @DisplayName("REC-02 - GET /recomendaciones/{ciudad} con 18C y Rain")
    void recomendar_Rain18_RetornaRecomendacionLluvia() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Londres", null))
                .thenReturn(crearClima(18.0, "Rain", "light rain"));

        assertRecomendacion("Londres", 18.0, "Rain", "light rain",
                ROPA_RAIN, ACCESORIOS_RAIN, MENSAJE_RAIN + " " + TEMP_FRESCO);
    }

    @Test
    @DisplayName("REC-03 - GET /recomendaciones/{ciudad} con 20C y Thunderstorm")
    void recomendar_Thunderstorm20_RetornaRecomendacionTormenta() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Xalapa", null))
                .thenReturn(crearClima(20.0, "Thunderstorm", "thunderstorm"));

        assertRecomendacion("Xalapa", 20.0, "Thunderstorm", "thunderstorm",
                ROPA_TORMENTA, ACCESORIOS_TORMENTA, MENSAJE_TORMENTA + " " + TEMP_FRESCO);
    }

    @Test
    @DisplayName("REC-04 - GET /recomendaciones/{ciudad} con 19C y Clouds")
    void recomendar_Clouds19_RetornaRecomendacionNublado() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Puebla", null))
                .thenReturn(crearClima(19.0, "Clouds", "cloudy"));

        assertRecomendacion("Puebla", 19.0, "Clouds", "cloudy",
                ROPA_CLOUDS, ACCESORIOS_CLOUDS, MENSAJE_CLOUDS + " " + TEMP_FRESCO);
    }

    @Test
    @DisplayName("REC-05 - GET /recomendaciones/{ciudad} con -1C y Snow")
    void recomendar_SnowMenos1_RetornaRecomendacionNieve() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Toluca", null))
                .thenReturn(crearClima(-1.0, "Snow", "snow"));

        assertRecomendacion("Toluca", -1.0, "Snow", "snow",
                ROPA_SNOW, ACCESORIOS_SNOW, MENSAJE_SNOW + " " + TEMP_BAJO_CERO);
    }

    @Test
    @DisplayName("REC-06 - GET /recomendaciones/{ciudad} con 14C y fog")
    void recomendar_Fog14_RetornaRecomendacionNieblaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Orizaba", null))
                .thenReturn(crearClima(14.0, "fog", "fog"));

        assertRecomendacion("Orizaba", 14.0, "fog", "fog",
                ROPA_BRUMA, ACCESORIOS_BRUMA, MENSAJE_BRUMA + " " + TEMP_FRIO);
    }

    @Test
    @DisplayName("REC-07 - GET /recomendaciones/{ciudad} con 40C y Clear")
    void recomendar_Clear40_RetornaRecomendacionTemperaturaExtrema() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Mexicali", null))
                .thenReturn(crearClima(40.0, "Clear", "clear sky"));

        assertRecomendacion("Mexicali", 40.0, "Clear", "clear sky",
                ROPA_CLEAR, ACCESORIOS_CLEAR, MENSAJE_CLEAR + " " + TEMP_EXTREMA);
    }

    @Test
    @DisplayName("REC-08 - GET /recomendaciones/{ciudad} con 35C y Clear")
    void recomendar_Clear35_RetornaRecomendacionMuchoCalor() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Hermosillo", null))
                .thenReturn(crearClima(35.0, "Clear", "clear sky"));

        assertRecomendacion("Hermosillo", 35.0, "Clear", "clear sky",
                ROPA_CLEAR, ACCESORIOS_CLEAR, MENSAJE_CLEAR + " " + TEMP_MUCHO_CALOR);
    }

    @Test
    @DisplayName("REC-09 - GET /recomendaciones/{ciudad} con 30C y Mist")
    void recomendar_Mist30_RetornaRecomendacionBrumaCalor() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Veracruz", null))
                .thenReturn(crearClima(30.0, "Mist", "mist"));

        assertRecomendacion("Veracruz", 30.0, "Mist", "mist",
                ROPA_BRUMA, ACCESORIOS_BRUMA, MENSAJE_BRUMA + " " + TEMP_CALUROSO);
    }

    @Test
    @DisplayName("REC-10 - GET /recomendaciones/{ciudad} con 10C y Haze")
    void recomendar_Haze10_RetornaRecomendacionBrumaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Perote", null))
                .thenReturn(crearClima(10.0, "Haze", "haze"));

        assertRecomendacion("Perote", 10.0, "Haze", "haze",
                ROPA_BRUMA, ACCESORIOS_BRUMA, MENSAJE_BRUMA + " " + TEMP_FRIO);
    }

    @Test
    @DisplayName("REC-11 - GET /recomendaciones/{ciudad} con 24C y Clear")
    void recomendar_Clear24_RetornaRecomendacionCalida() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Merida", null))
                .thenReturn(crearClima(24.0, "Clear", "clear sky"));

        assertRecomendacion("Merida", 24.0, "Clear", "clear sky",
                ROPA_CLEAR, ACCESORIOS_CLEAR, MENSAJE_CLEAR + " " + TEMP_CALIDO);
    }

    @Test
    @DisplayName("REC-12 - GET /recomendaciones/{ciudad} con 23C y Clouds")
    void recomendar_Clouds23_RetornaRecomendacionFresca() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Queretaro", null))
                .thenReturn(crearClima(23.0, "Clouds", "cloudy"));

        assertRecomendacion("Queretaro", 23.0, "Clouds", "cloudy",
                ROPA_CLOUDS, ACCESORIOS_CLOUDS, MENSAJE_CLOUDS + " " + TEMP_FRESCO);
    }

    @Test
    @DisplayName("REC-13 - GET /recomendaciones/{ciudad} con 5C y Clear")
    void recomendar_Clear5_RetornaRecomendacionFrioExtremo() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Chihuahua", null))
                .thenReturn(crearClima(5.0, "Clear", "clear sky"));

        assertRecomendacion("Chihuahua", 5.0, "Clear", "clear sky",
                ROPA_CLEAR, ACCESORIOS_CLEAR, MENSAJE_CLEAR + " " + TEMP_FRIO_EXTREMO);
    }

    @Test
    @DisplayName("REC-14 - GET /recomendaciones/{ciudad} con 25C y Drizzle")
    void recomendar_Drizzle25_RetornaRecomendacionLluvia() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Cordoba", null))
                .thenReturn(crearClima(25.0, "Drizzle", "drizzle"));

        assertRecomendacion("Cordoba", 25.0, "Drizzle", "drizzle",
                ROPA_RAIN, ACCESORIOS_RAIN, MENSAJE_RAIN + " " + TEMP_CALIDO);
    }

    @Test
    @DisplayName("REC-15 - GET /recomendaciones/{ciudad} con 17C y Mist")
    void recomendar_Mist17_RetornaRecomendacionBrumaFrio() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Coatepec", null))
                .thenReturn(crearClima(17.0, "Mist", "mist"));

        assertRecomendacion("Coatepec", 17.0, "Mist", "mist",
                ROPA_BRUMA, ACCESORIOS_BRUMA, MENSAJE_BRUMA + " " + TEMP_FRIO);
    }

    @Test
    @DisplayName("REC-16 - GET /recomendaciones/{ciudad} con 0C y Snow")
    void recomendar_Snow0_RetornaRecomendacionNieveFrioExtremo() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("Nevado", null))
                .thenReturn(crearClima(0.0, "Snow", "snow"));

        assertRecomendacion("Nevado", 0.0, "Snow", "snow",
                ROPA_SNOW, ACCESORIOS_SNOW, MENSAJE_SNOW + " " + TEMP_FRIO_EXTREMO);
    }

    @Test
    @DisplayName("REC-17 - GET /recomendaciones/{ciudad} con nombre de ciudad inexistente")
    void recomendar_CiudadInexistente_RetornaMensajeDeCiudadNoEncontrada() throws Exception {
        when(weatherService.obtenerClimaPorCiudad("CiudadInexistente", null))
                .thenThrow(ciudadNoEncontrada());

        mockMvc.perform(get("/recomendaciones/CiudadInexistente").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_CIUDAD_NO_ENCONTRADA));
    }

    @Test
    @DisplayName("REC-18 - GET /recomendaciones/{ciudad} sin iniciar sesion")
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
        WeatherResponseDTO response = new WeatherResponseDTO();

        WeatherResponseDTO.MainData mainData = new WeatherResponseDTO.MainData();
        mainData.setTemp(temperatura);
        response.setMain(mainData);

        WeatherResponseDTO.Weather weather = new WeatherResponseDTO.Weather();
        weather.setMain(clima);
        weather.setDescription(descripcion);
        response.setWeather(List.of(weather));

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
