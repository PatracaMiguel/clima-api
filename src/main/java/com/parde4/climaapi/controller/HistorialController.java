package com.parde4.climaapi.controller;

import com.parde4.climaapi.model.Historial;
import com.parde4.climaapi.service.HistorialService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/historial")
@CrossOrigin(origins = "*")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @GetMapping
    public List<Historial> obtenerHistorial(HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión");
        }

        return historialService.obtenerPorUsuario(usuarioId);
    }

    @DeleteMapping
    public Map<String, String> eliminarHistorial(HttpSession session) {

        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            throw new RuntimeException("Debe iniciar sesión antes de eliminar el historial");
        }

        historialService.eliminarHistorialPorUsuario(usuarioId);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Historial eliminado correctamente");

        return response;
    }
}