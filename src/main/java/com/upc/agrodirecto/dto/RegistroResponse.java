package com.upc.agrodirecto.dto;

public record RegistroResponse(
		Integer idUsuario,
		String correo,
		String estadoCuenta,
		Integer idRol
) {}
