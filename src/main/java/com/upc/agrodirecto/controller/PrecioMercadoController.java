package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.entidades.PrecioMercado;
import com.upc.agrodirecto.servicios.PrecioMercadoServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/precios-mercado")
public class PrecioMercadoController {
	@Autowired
	private PrecioMercadoServicios precioMercadoServicios;

	@GetMapping
	public List<PrecioMercado> listar(@RequestParam(required = false) String fecha) {
		LocalDate fechaConsulta = (fecha == null || fecha.isBlank() || "hoy".equalsIgnoreCase(fecha))
				? LocalDate.now() : LocalDate.parse(fecha);
		return precioMercadoServicios.listarPorFecha(fechaConsulta);
	}

	@GetMapping("/ultimo/{idCultivo}")
	public PrecioMercado ultimo(@PathVariable Integer idCultivo) {
		return precioMercadoServicios.ultimoPrecio(idCultivo);
	}
}
