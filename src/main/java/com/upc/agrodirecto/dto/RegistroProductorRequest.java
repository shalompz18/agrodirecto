package com.upc.agrodirecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroProductorRequest(
        @NotBlank @Size(max = 120) String nombreCompleto,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(min = 6, max = 72) String contrasena,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 dígitos") String dni,
        @NotBlank @Size(max = 100) String distrito
) {}
