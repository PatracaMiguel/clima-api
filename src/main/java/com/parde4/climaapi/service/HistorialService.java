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

    public void guardar(String ciudad, Double temperatura , Integer usuarioId) {
        Historial historial = new Historial();
        historial.setCiudad(ciudad);
        historial.setTemperatura(temperatura);
        historial.setFechaConsulta(LocalDateTime.now());
        historial.setUsuarioId(usuarioId);
        historialRepository.save(historial);
    }

    public java.util.List<Historial> obtenerPorUsuario(Integer usuarioId) {
        return historialRepository.findByUsuarioIdAndDeletedAtIsNull(usuarioId);
    }

    public void eliminarHistorialPorUsuario(Integer usuarioId) {

    java.util.List<Historial> historial =
            historialRepository.findByUsuarioIdAndDeletedAtIsNull(usuarioId);

    historial.forEach(item -> item.setDeletedAt(LocalDateTime.now()));

    historialRepository.saveAll(historial);
}
}
