package com.upc.agrodirecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportarRetrasoRequest(@NotBlank @Size(max = 250) String motivo) {}
