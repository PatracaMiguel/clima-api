package com.parde4.climaapi.dto;

public class LoginResponse {

    private String mensaje;
    private Integer idUsuario;
    private String nombre;
    private String correo;

    public LoginResponse() {
    }

    public LoginResponse(String mensaje, Integer idUsuario, String nombre, String correo) {
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}