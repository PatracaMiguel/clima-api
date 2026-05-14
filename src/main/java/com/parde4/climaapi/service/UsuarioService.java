package com.parde4.climaapi.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {

        if (usuarioRepository.findByCorreoAndDeletedAtIsNull(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setFechaCreado(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuario(Integer id) {
        return usuarioRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizarUsuario(Integer id, Usuario datosActualizados) {

        Usuario usuario = usuarioRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioRepository.findByCorreoAndDeletedAtIsNull(datosActualizados.getCorreo())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new RuntimeException("El correo ya está registrado");
                    }
                });

        usuario.setNombre(datosActualizados.getNombre());
        usuario.setCorreo(datosActualizados.getCorreo());
        usuario.setContrasena(datosActualizados.getContrasena());

        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Integer id) {

        Usuario usuario = usuarioRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}