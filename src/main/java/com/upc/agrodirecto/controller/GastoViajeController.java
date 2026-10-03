package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.GastoViajeRequest;
import com.upc.agrodirecto.entidades.GastoViaje;
import com.upc.agrodirecto.servicios.GastoViajeServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lotes/{idLote}/gastos")
public class GastoViajeController {
	@Autowired
	private GastoViajeServicios gastoViajeServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@PostMapping
	public ResponseEntity<GastoViaje> registrar(@PathVariable Integer idLote, @Valid @RequestBody GastoViajeRequest req) {
		GastoViaje gasto = gastoViajeServicios.registrarGasto(idLote, req, authenticatedUserService.getCurrentUser().getIdUsuario());
		return ResponseEntity.status(HttpStatus.CREATED).body(gasto);
	}

	@GetMapping
	public Map<String, Object> listar(@PathVariable Integer idLote) {
		List<GastoViaje> gastos = gastoViajeServicios.listarPorLote(idLote, authenticatedUserService.getCurrentUser().getIdUsuario());
		double total = gastoViajeServicios.totalGastos(idLote, authenticatedUserService.getCurrentUser().getIdUsuario());
		return Map.of("idLote", idLote, "totalGastos", total, "gastos", gastos);
	}
}
