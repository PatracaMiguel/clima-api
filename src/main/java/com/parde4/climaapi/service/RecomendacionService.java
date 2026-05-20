package com.parde4.climaapi.service;

import com.parde4.climaapi.dto.RecomendacionResponseDTO;
import com.parde4.climaapi.dto.WeatherResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecomendacionService {

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

        String ropa;
        String accesorios;
        String mensajeClima;

        if (clima.equalsIgnoreCase("Rain")) {
            ropa = "Se recomienda usar impermeable o chamarra resistente al agua, pantalon de secado rapido y zapatos con suela antideslizante";
            accesorios = "Lleve sombrilla, mochila impermeable, cubre zapatos y una bolsa plastica para proteger celular o documentos si lleva alguno";
            mensajeClima = "Esta lloviendo afuera , salga con tiempo y evita zonas encharcadas ";
        } else if (clima.equalsIgnoreCase("Thunderstorm")) {
            ropa = "Se recomienda usar impermeable, ropa comoda de secado rapido y calzado cerrado con buena suela.";
            accesorios = "Lleve sombrilla resistente, mochila impermeable y linterna pequena si vas a estar fuera.";
            mensajeClima = "Hay tormenta ,  evite salir si no es necesario, no se resguardes bajo arboles y mantengase atento a los cambios del clima.";
        } else if (clima.equalsIgnoreCase("Clear")) {
            ropa = "Se recomienda usar ropa ligera, fresca y de colores claros como  playera de algodon, short o pantalon ligero.";
            accesorios = "Lleve lentes de sol, gorra o sombrero, bloqueador solar y botella de agua.";
            mensajeClima = "El cielo esta despejado , aproveche el buen clima, pero protegase del sol.";
        } else if (clima.equalsIgnoreCase("Clouds")) {
            ropa = "Se recomienda usar ropa comoda y ligera puede usar una chamarra delgada por si baja la temperatura.";
            accesorios = "Lleve lentes de sol si hay calor intenso y somrbrilla pequena si hay nubes negras";
            mensajeClima = "El clima esta nublado pero es mejor salir preparado por si cambia durante el dia.";
        } else if (clima.equalsIgnoreCase("Snow")) {
            ropa = "Se recomienda usar sueter grueso, ropa termica, pantalon resistente al frio y botas.";
            accesorios = "Lleve guantes, gorro, bufanda y calcetines termicos.";
            mensajeClima = "Esta nevando abriguese bien y camine con precaucion por superficies resbalosas.";
        } else if (clima.equalsIgnoreCase("Mist") || clima.equalsIgnoreCase("Fog") || clima.equalsIgnoreCase("Haze")) {
            ropa = "Se recomienda usar ropa comoda y una chamarra ligera si hay humedad o baja visibilidad.";
            accesorios = "Lleve cubrebocas si hay bruma, luces o reflejantes si caminas, y maneje con precaucion.";
            mensajeClima = "Hay niebla o bruma, si conduce reduja la velocidad, manten distancia y evite zonas con poca visibilidad.";
        } else {
            ropa = "Se recomienda usar ropa comoda adecuada para salir durante el dia.";
            accesorios = "Lleva agua y revise el clima antes de salir.";
            mensajeClima = "El clima puede variar,  sal preparado para cambios repentinos.";
        }

        String mensajeTemperatura;

        if (temperatura >= 40) {
            mensajeTemperatura = "Temperatura extremadamente alta evita exponerte al sol, toma agua constantemente y procura estar en sombra o lugares ventilados.";
        } else if (temperatura >= 35) {
            mensajeTemperatura = "Hace mucho calor afuera usa bloqueador, hidratate y evita actividades pesadas al aire libre.";
        } else if (temperatura >= 30) {
            mensajeTemperatura = "El clima esta caluroso usa ropa fresca, lentes de sol y toma agua durante el dia.";
        } else if (temperatura >= 24) {
            mensajeTemperatura = "El clima es calido y agradable sal con ropa ligera, pero no olvides protegerte del sol.";
        } else if (temperatura >= 18) {
            mensajeTemperatura = "El clima es fresco usa ropa comoda, una chamarra ligera puede ser util si estaras fuera por la tarde.";
        } else if (temperatura >= 10) {
            mensajeTemperatura = "El clima es frio lleva sueter o chamarra gruesa para evitar cambios bruscos de temperatura.";
        } else if (temperatura >= 0) {
            mensajeTemperatura = "El clima es fio extremo usa chamarra o sueter grueso, pantalon largo y calzado cerrado.";
        } else {
            mensajeTemperatura = "La temperatura es bajo cero Usa varias capas de ropa, abrigo grueso, guantes y gorro.";
        }

        recomendacion.setRecomendacionRopa(ropa);
        recomendacion.setRecomendacionAccesorios(accesorios);
        recomendacion.setMensaje(mensajeClima + " " + mensajeTemperatura);

        return recomendacion;
    }
}