package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.SuscripcionRequest;
import com.upc.agrodirecto.dto.SuscripcionResponse;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import com.upc.agrodirecto.servicios.SuscripcionServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios/{idUsuario}/suscripcion-premium")
public class SuscripcionController {
	@Autowired
	private SuscripcionServicios suscripcionServicios;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@PostMapping
	public SuscripcionResponse contratar(@PathVariable Integer idUsuario, @Valid @RequestBody SuscripcionRequest req) {
		verificarPropietario(idUsuario);
		Usuario u = suscripcionServicios.contratarPremium(idUsuario);
		return new SuscripcionResponse(u.getIdUsuario(), u.getPremiumHasta(), true);
	}

	@GetMapping
	public SuscripcionResponse consultar(@PathVariable Integer idUsuario) {
		verificarPropietario(idUsuario);
		Usuario u = usuarioRepositorio.findById(idUsuario).orElseThrow();
		boolean esPremium = suscripcionServicios.esPremium(u);
		return new SuscripcionResponse(u.getIdUsuario(), u.getPremiumHasta(), esPremium);
	}

	private void verificarPropietario(Integer idUsuario) {
		if (!authenticatedUserService.getCurrentUser().getIdUsuario().equals(idUsuario)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo puedes gestionar tu propia suscripción");
		}
		Integer idRol = authenticatedUserService.getCurrentUser().getRol().getIdRol();
		if (idRol != 1 && idRol != 2) {
			throw new org.springframework.security.access.AccessDeniedException("Solo productores y transportistas pueden contratar Premium");
		}
	}
}
