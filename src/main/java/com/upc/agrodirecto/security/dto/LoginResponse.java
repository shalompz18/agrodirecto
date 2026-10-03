package com.upc.agrodirecto.security.dto;

public record LoginResponse(
        String token,
        String tipo,
        Integer idUsuario,
        String correo,
        String rol,
        String estadoCuenta
) {}
