package com.parde4.climaapi.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "Sesión cerrada correctamente";
    }
}