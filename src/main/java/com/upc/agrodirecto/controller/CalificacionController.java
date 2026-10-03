package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.CalificacionRequest;
import com.upc.agrodirecto.entidades.Calificacion;
import com.upc.agrodirecto.servicios.CalificacionServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calificaciones")
public class CalificacionController {
	@Autowired
	private CalificacionServicios calificacionServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@PostMapping
	public ResponseEntity<Calificacion> calificar(@RequestBody CalificacionRequest req) {
		Calificacion c = calificacionServicios.calificar(req, authenticatedUserService.getCurrentUser().getIdUsuario());
		return ResponseEntity.status(HttpStatus.CREATED).body(c);
	}

	@GetMapping("/lote/{idLote}")
	public List<Calificacion> porLote(@PathVariable Integer idLote) {
		return calificacionServicios.listarPorLote(idLote);
	}
}
