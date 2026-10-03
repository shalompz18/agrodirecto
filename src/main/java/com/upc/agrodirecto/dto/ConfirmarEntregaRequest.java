package com.upc.agrodirecto.dto;

import java.time.LocalDateTime;

public record ConfirmarEntregaRequest(String codigoValidacion, LocalDateTime fechaLlegada) {}
