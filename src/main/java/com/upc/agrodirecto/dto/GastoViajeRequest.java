package com.upc.agrodirecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// EP35
public record GastoViajeRequest(@NotBlank @Size(max = 120) String concepto,
                                @NotNull @Positive Double monto) {}
