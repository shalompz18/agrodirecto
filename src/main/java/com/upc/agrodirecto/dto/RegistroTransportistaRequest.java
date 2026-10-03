package com.upc.agrodirecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RegistroTransportistaRequest(
        @NotBlank @Size(max = 120) String nombreCompleto,
        @NotBlank @Email @Size(max = 150) String correo,
        @NotBlank @Size(min = 6, max = 72) String contrasena,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "El RUC debe tener exactamente 11 dígitos") String ruc,
        @NotBlank @Size(max = 10) String placa,
        @NotNull @PositiveOrZero Double capacidadToneladas,
        @NotBlank @Size(max = 20) String licencia
) {}
