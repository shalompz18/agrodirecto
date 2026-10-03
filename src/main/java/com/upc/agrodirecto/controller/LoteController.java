package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.ConfirmarEntregaRequest;
import com.upc.agrodirecto.dto.ReportarRetrasoRequest;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.servicios.LoteServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/lotes")
public class LoteController {
	@Autowired
	private LoteServicios loteServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@GetMapping("/{idLote}")
	public Lote detalle(@PathVariable Integer idLote) {
		return loteServicios.detalleConCargas(idLote);
	}

	@GetMapping("/{idLote}/financiero")
	public Lote desgloseFinanciero(@PathVariable Integer idLote) {
		// Se reutiliza la misma entidad Lote; ya trae fleteTotal, porcentajeComision,
		// montoComision, montoNeto y estadoPago.
		return loteServicios.seguimiento(idLote);
	}

	@GetMapping("/{idLote}/seguimiento")
	public Lote seguimiento(@PathVariable Integer idLote) {
		return loteServicios.seguimiento(idLote);
	}

	@PatchMapping("/{idLote}/confirmar-entrega")
	public Lote confirmarEntrega(@PathVariable Integer idLote, @Valid @RequestBody ConfirmarEntregaRequest req) {
		return loteServicios.confirmarEntrega(idLote, req, authenticatedUserService.getCurrentUser().getIdUsuario());
	}

	@PatchMapping("/{idLote}/reportar-retraso")
	public Lote reportarRetraso(@PathVariable Integer idLote, @Valid @RequestBody ReportarRetrasoRequest req) {
		return loteServicios.reportarRetraso(idLote, req, authenticatedUserService.getCurrentUser().getIdUsuario());
	}
}
