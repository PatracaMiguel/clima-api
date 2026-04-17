package com.parde4.climaapi.service;

import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
            throw new RuntimeException("El correo ya está registrado");
            
        }

        usuario.setFechaCreado(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    public Usuario actualizarUsuario(Long id, Usuario datosActualizados) {

    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Usuario existente = usuarioRepository.findByCorreo(datosActualizados.getCorreo());
    if (existente != null && !existente.getId().equals(id)) {
        throw new RuntimeException("El correo ya está registrado");
    }

    usuario.setNombre(datosActualizados.getNombre());
    usuario.setCorreo(datosActualizados.getCorreo());
    usuario.setContrasena(datosActualizados.getContrasena());

    return usuarioRepository.save(usuario);
    }
}
