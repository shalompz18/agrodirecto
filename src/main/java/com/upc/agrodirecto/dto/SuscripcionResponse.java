package com.upc.agrodirecto.dto;

import java.time.LocalDateTime;

public record SuscripcionResponse(Integer idUsuario, LocalDateTime premiumHasta, Boolean esPremium) {}
