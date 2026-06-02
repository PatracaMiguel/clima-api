package com.parde4.climaapi.service;

import com.parde4.climaapi.dto.RecomendacionResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecomendacionService {

    private static final double VELOCIDAD_VIENTO_FUERTE = 10.0;

    private static final String MODIFICADOR_LLUVIA = "Alerta de lluvia: Asegúrate de llevar un paraguas resistente y una chamarra impermeable con capucha. Usa calzado impermeable.";
    private static final String MODIFICADOR_NIEVE = "Alerta de nieve: Usa ropa impermeable resistente al agua para que la nieve no te moje al derretirse. Usa botas con suela antideslizante para evitar resbalones en el hielo.";
    private static final String MODIFICADOR_VIENTO = "Alerta de viento: Te recomendamos usar una chaqueta rompevientos y evitar faldas o vestidos. Si tienes el cabello largo usa una liga o pinza.";

    @Autowired
    private WeatherService weatherService;

    public RecomendacionResponseDTO recomendarClimaCiudad(String ciudad) {

        WeatherResponseDTO climaCiudad = weatherService.obtenerClimaPorCiudad(ciudad, null);

        double temperatura = climaCiudad.getMain().getTemp();
        String clima = climaCiudad.getWeather().get(0).getMain();
        String descripcion = climaCiudad.getWeather().get(0).getDescription();

        RecomendacionResponseDTO recomendacion = new RecomendacionResponseDTO();
        recomendacion.setCiudad(ciudad);
        recomendacion.setTemperatura(temperatura);
        recomendacion.setClima(clima);
        recomendacion.setDescripcion(descripcion);

        String recomendacionTemperatura = recomendacionPorTemperatura(temperatura);
        String modificadores = modificadoresPorClima(clima, climaCiudad);

        recomendacion.setRecomendacionRopa(recomendacionTemperatura);
        recomendacion.setRecomendacionAccesorios(modificadores);
        recomendacion.setMensaje(modificadores);

        return recomendacion;
    }

    private String recomendacionPorTemperatura(double temperatura) {
        if (temperatura < 0) {
            return "Hace un frío extremo afuera. Usa ropa térmica seguido de un suéter grueso de lana y un abrigo térmico o parka impermeable. Usar pantalones abrigados, botas con suela antideslizante, gorro, bufanda y guantes impermeables.";
        }

        if (temperatura <= 12) {
            return "El día está bastante frío. Te recomendamos usar una camiseta de manga larga, un suéter o sudadera, y una chamarra, combínalo con jeans gruesos, botas o tenis cerrados de piel. Usar un gorro ligero y una bufanda.";
        }

        if (temperatura <= 20) {
            return "El clima está fresco y agradable. Te recomendamos usar prendas fáciles de quitar y poner por si cambia el día. Una playera o camisa combinada con una chaqueta ligera como una chamarra de mezclilla o un blazer. Usa jeans o pantalones casuales y tenis.";
        }

        if (temperatura <= 28) {
            return "Es un día cálido. Te recomendamos usar ropa fresca y transpirable de algodón o lino: playeras de manga corta, blusas ligeras, bermudas, shorts o vestidos cómodos. Usar tenis ligeros o sandalias, llevar tus lentes de sol y una gorra, si vas a caminar bajo el sol usa protector solar.";
        }

        return "Calor extremo, te recomendamos mantenerte fresco con ropa muy holgada y de telas ultraligeras, preferentemente en colores claros para no absorber el calor. Usar shorts, faldas y playeras de tirantes, junto con sandalias abiertas. Es obligatorio usar protector solar, lentes de sol y una gorra.";
    }

    private String modificadoresPorClima(String clima, WeatherResponseDTO climaCiudad) {
        StringBuilder modificadores = new StringBuilder();

        if (esLluvioso(clima)) {
            modificadores.append(MODIFICADOR_LLUVIA);
        }

        if (esNevado(clima)) {
            agregarModificador(modificadores, MODIFICADOR_NIEVE);
        }

        if (hayVientoFuerte(climaCiudad)) {
            agregarModificador(modificadores, MODIFICADOR_VIENTO);
        }

        return modificadores.toString();
    }

    private boolean esLluvioso(String clima) {
        return clima.equalsIgnoreCase("Rain")
                || clima.equalsIgnoreCase("Drizzle")
                || clima.equalsIgnoreCase("Thunderstorm");
    }

    private boolean esNevado(String clima) {
        return clima.equalsIgnoreCase("Snow");
    }

    private boolean hayVientoFuerte(WeatherResponseDTO climaCiudad) {
        return climaCiudad.getWind() != null && climaCiudad.getWind().getSpeed() >= VELOCIDAD_VIENTO_FUERTE;
    }

    private void agregarModificador(StringBuilder modificadores, String modificador) {
        if (!modificadores.isEmpty()) {
            modificadores.append(" ");
        }
        modificadores.append(modificador);
    }
}
