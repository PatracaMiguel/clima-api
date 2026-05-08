package com.parde4.climaapi.service;

import com.parde4.climaapi.dto.LoginRequest;
import com.parde4.climaapi.dto.LoginResponse;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo());

        if (usuario == null) {
            throw new RuntimeException("El correo no está registrado");
        }

        if (!usuario.getContrasena().equals(request.getContrasena())) {
            throw new RuntimeException("La contraseña es incorrecta");
        }

        return new LoginResponse(
                "Inicio de sesión exitoso",
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo()
        );
    }
}