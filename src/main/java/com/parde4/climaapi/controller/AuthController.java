package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.LoginRequest;
import com.parde4.climaapi.dto.LoginResponse;
import com.parde4.climaapi.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = authService.login(request);

        session.setAttribute("usuarioId", response.getIdUsuario());

        return ResponseEntity.ok(response);
    }
}