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

        RecomendacionPorClima recomendacionClima = recomendacionPorClima(clima);
        RecomendacionPorTemperatura recomendacionTemperatura = recomendacionPorTemperatura(temperatura);

        recomendacion.setRecomendacionRopa(recomendacionClima.ropa());
        recomendacion.setRecomendacionAccesorios(recomendacionClima.accesorios());
        recomendacion.setMensaje(recomendacionClima.mensaje() + " " + recomendacionTemperatura.mensaje());

        return recomendacion;
    }

    private RecomendacionPorClima recomendacionPorClima(String clima) {
        if (clima.equalsIgnoreCase("Rain") || clima.equalsIgnoreCase("Drizzle")) {
            return new RecomendacionPorClima(
                    "Usa impermeable o chamarra resistente al agua, pantalon que seque rapido y botas de lluvia o zapatos cerrados con suela antideslizante.",
                    "Lleva sombrilla, mochila impermeable, una bolsa para proteger celular o documentos y una muda ligera si vas a estar mucho tiempo fuera.",
                    "Hay lluvia; sal con tiempo, evita zonas encharcadas y cuida los cambios de temperatura.");
        }

        if (clima.equalsIgnoreCase("Thunderstorm")) {
            return new RecomendacionPorClima(
                    "Usa impermeable, ropa comoda de secado rapido y calzado cerrado con buena suela.",
                    "Lleva sombrilla resistente, mochila impermeable, linterna pequena y evita cargar objetos metalicos expuestos.",
                    "Hay tormenta; si puedes, espera a que baje la intensidad y evita resguardarte debajo de arboles.");
        }

        if (clima.equalsIgnoreCase("Clear")) {
            return new RecomendacionPorClima(
                    "Usa ropa fresca, ligera y de colores claros; una playera de algodon o lino y pantalon ligero funcionan bien.",
                    "Lleva lentes de sol, gorra o sombrero, protector solar y una botella de agua.",
                    "El cielo esta despejado; aprovecha el dia, pero protege tu piel y mantente hidratado.");
        }

        if (clima.equalsIgnoreCase("Clouds")) {
            return new RecomendacionPorClima(
                    "Usa ropa comoda en capas: playera ligera y una chamarra delgada por si baja la temperatura.",
                    "Lleva lentes de sol si hay resolana y una sombrilla compacta si las nubes se ven densas.",
                    "El clima esta nublado; conviene salir preparado por si cambia durante el dia.");
        }

        if (clima.equalsIgnoreCase("Snow")) {
            return new RecomendacionPorClima(
                    "Usa ropa termica, sueter grueso, chamarra abrigadora, pantalon resistente al frio y botas.",
                    "Lleva guantes, gorro, bufanda, calcetines termicos y protector labial.",
                    "Hay nieve; abrigate bien y camina con cuidado en superficies resbalosas.");
        }

        if (clima.equalsIgnoreCase("Mist") || clima.equalsIgnoreCase("Fog") || clima.equalsIgnoreCase("Haze")) {
            return new RecomendacionPorClima(
                    "Usa ropa comoda y una chamarra ligera si hay humedad o baja visibilidad.",
                    "Lleva cubrebocas si hay bruma, luces o reflejantes si caminas, y maneja con precaucion.",
                    "Hay niebla o bruma; reduce la velocidad, manten distancia y evita zonas con poca visibilidad.");
        }

        return new RecomendacionPorClima(
                "Usa ropa comoda adecuada para salir durante el dia y considera llevar una capa extra por si cambia el clima.",
                "Lleva agua, protector solar basico y revisa el clima nuevamente antes de salir.",
                "El clima puede variar; sal preparado para cambios repentinos.");
    }

    private RecomendacionPorTemperatura recomendacionPorTemperatura(double temperatura) {
        if (temperatura >= 40) {
            return new RecomendacionPorTemperatura(
                    "La temperatura es extremadamente alta: evita el sol directo, usa ropa muy ligera y transpirable, toma agua constantemente y busca sombra o lugares ventilados.");
        }

        if (temperatura >= 35) {
            return new RecomendacionPorTemperatura(
                    "Hace mucho calor: usa ropa suelta y fresca, protector solar, lentes de sol, gorra y evita actividad pesada al aire libre.");
        }

        if (temperatura >= 30) {
            return new RecomendacionPorTemperatura(
                    "El dia esta caluroso: usa ropa ligera, colores claros, lentes de sol y toma agua durante el dia.");
        }

        if (temperatura >= 24) {
            return new RecomendacionPorTemperatura(
                    "La temperatura es calida y agradable: usa ropa ligera, pero no olvides protector solar si estaras al aire libre.");
        }

        if (temperatura >= 18) {
            return new RecomendacionPorTemperatura(
                    "El clima esta fresco: usa ropa comoda y lleva una chamarra ligera si estaras fuera por la tarde o noche.");
        }

        if (temperatura >= 10) {
            return new RecomendacionPorTemperatura(
                    "Hace frio: lleva sueter o chamarra, pantalon largo y calzado cerrado para evitar cambios bruscos de temperatura.");
        }

        if (temperatura >= 0) {
            return new RecomendacionPorTemperatura(
                    "Hace frio intenso: usa chamarra gruesa, sueter, pantalon largo, calcetines abrigadores y calzado cerrado.");
        }

        return new RecomendacionPorTemperatura(
                "La temperatura esta bajo cero: usa varias capas de ropa, abrigo grueso, guantes, gorro y bufanda.");
    }

    private record RecomendacionPorClima(String ropa, String accesorios, String mensaje) {
    }

    private record RecomendacionPorTemperatura(String mensaje) {
    }
}
