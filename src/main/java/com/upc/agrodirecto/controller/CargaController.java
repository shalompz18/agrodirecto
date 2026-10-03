package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.AsignarRetornoRequest;
import com.upc.agrodirecto.dto.PublicarCargaRequest;
import com.upc.agrodirecto.entidades.Carga;
import com.upc.agrodirecto.servicios.CargaServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargas")
public class CargaController {
	@Autowired
	private CargaServicios cargaServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@PostMapping
	public ResponseEntity<Carga> publicar(@Valid @RequestBody PublicarCargaRequest req) {
		Integer idProductor = authenticatedUserService.getCurrentUser().getIdUsuario();
		Carga carga = cargaServicios.publicarCarga(idProductor, req);
		return ResponseEntity.status(HttpStatus.CREATED).body(carga);
	}

	@PatchMapping("/{idCarga}/cancelar")
	public Carga cancelar(@PathVariable Integer idCarga) {
		return cargaServicios.cancelarCarga(idCarga, authenticatedUserService.getCurrentUser().getIdUsuario());
	}

	@GetMapping
	public List<Carga> listarPorEstado(@RequestParam(required = false) String estado) {
		return cargaServicios.listarPorEstado(estado);
	}

	@GetMapping("/retorno")
	public List<Carga> listarRetorno() {
		return cargaServicios.listarCargasDeRetorno();
	}

	@PatchMapping("/{idCarga}/asignar-retorno")
	public Carga asignarRetorno(@PathVariable Integer idCarga, @RequestBody AsignarRetornoRequest req) {
		return cargaServicios.asignarRetorno(idCarga, req.idLote(), authenticatedUserService.getCurrentUser().getIdUsuario());
	}
}
