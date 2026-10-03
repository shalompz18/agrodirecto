package com.upc.agrodirecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PublicarCargaRequest(
        @NotNull Integer idCultivo,
        @NotNull @Positive Double pesoKg,
        @NotBlank @Pattern(regexp = "perecible|seco", message = "tipoCarga debe ser perecible o seco") String tipoCarga,
        @NotBlank @Size(max = 150) String direccionDestino,
        @NotNull @Positive Double tarifaPropuesta,
        @NotNull LocalDate fechaRecojo,
        Boolean esUrgente
) {}
