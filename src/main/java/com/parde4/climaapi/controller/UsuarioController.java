package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.UsuarioRequestDTO;
import com.parde4.climaapi.dto.UsuarioResponseDTO;
import com.parde4.climaapi.dto.UsuarioUpdateDTO;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.service.UsuarioService;
import jakarta.validation.Valid;


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

        return mapToDTO(guardado);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtenerUsuario(@PathVariable Long id) {

        Usuario usuario = usuarioService.obtenerUsuario(id);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return mapToDTO(usuario);
    }

    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setFechaCreado(usuario.getFechaCreado());
        return dto;
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizarUsuario(
        @PathVariable Long id,
        @RequestBody @Valid UsuarioUpdateDTO dto) {

    Usuario usuario = new Usuario();
    usuario.setNombre(dto.getNombre());
    usuario.setCorreo(dto.getCorreo());
    usuario.setContrasena(dto.getContrasena());

    Usuario actualizado = usuarioService.actualizarUsuario(id, usuario);

    return mapToDTO(actualizado);
    }
}
