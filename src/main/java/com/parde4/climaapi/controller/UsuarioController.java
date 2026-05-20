package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.UsuarioRequestDTO;
import com.parde4.climaapi.dto.UsuarioResponseDTO;
import com.parde4.climaapi.dto.UsuarioUpdateDTO;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.service.UsuarioService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioResponseDTO crearUsuario(@RequestBody @Valid UsuarioRequestDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(dto.getContrasena());

        Usuario guardado = usuarioService.crearUsuario(usuario);

        return mapToDTO(guardado, "Usuario registrado correctamente");
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtenerUsuario(@PathVariable Integer id) {

        Usuario usuario = usuarioService.obtenerUsuario(id);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return mapToDTO(usuario, "");
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizarUsuario(
            @PathVariable Integer id,
            @RequestBody @Valid UsuarioUpdateDTO dto) {

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(dto.getContrasena());

        Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);

        return mapToDTO(actualizado, "Usuario actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Usuario eliminado correctamente");
        return response;
    }

    private UsuarioResponseDTO mapToDTO(Usuario usuario, String mensaje) {

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setMensaje(mensaje);
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setFechaCreado(usuario.getFechaCreado());

        return dto;
    }
}