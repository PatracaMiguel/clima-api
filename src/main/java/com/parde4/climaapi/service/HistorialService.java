package com.parde4.climaapi.service;

import com.parde4.climaapi.model.Historial;
import com.parde4.climaapi.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    public void guardar(String ciudad, Double temperatura) {
        Historial historial = new Historial();
        historial.setCiudad(ciudad);
        historial.setTemperatura(temperatura);
        historial.setFechaConsulta(LocalDateTime.now());
        historialRepository.save(historial);
    }
}
