package com.upc.agrodirecto.dto;

public record CalificacionRequest(
		Integer idLote,
		Short estrellas,
		String comentario,
		Boolean recomienda
) {}
