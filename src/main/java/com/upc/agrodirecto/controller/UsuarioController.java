package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.EstadoUsuarioRequest;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.servicios.LoteServicios;
import com.upc.agrodirecto.servicios.UsuarioServicios;
import com.upc.agrodirecto.servicios.ReporteServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UsuarioController {
	@Autowired
	private UsuarioServicios usuarioServicios;
	@Autowired
	private LoteServicios loteServicios;
	@Autowired
	private ReporteServicios reporteServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@GetMapping("/usuarios")
	public List<Usuario> listarPorRol(@RequestParam(required = false) Integer idRol) {
		return usuarioServicios.listarPorRol(idRol);
	}

	@PatchMapping("/usuarios/{idUsuario}/estado")
	public Usuario cambiarEstado(@PathVariable Integer idUsuario, @RequestBody EstadoUsuarioRequest req) {
		return usuarioServicios.cambiarEstado(idUsuario, req);
	}

	@GetMapping("/usuarios/{idUsuario}/transportista")
	public Usuario perfilTransportista(@PathVariable Integer idUsuario) {
		return usuarioServicios.perfilTransportista(idUsuario);
	}

	@GetMapping("/usuarios/{idUsuario}/administrador")
	public Usuario perfilAdministrador(@PathVariable Integer idUsuario) {
		verificarPropietario(idUsuario);
		return usuarioServicios.perfilAdministrador(idUsuario);
	}

	@GetMapping("/productores/{idProductor}/saldo")
	public Map<String, Object> saldoProductor(@PathVariable Integer idProductor) {
		verificarPropietario(idProductor);
		Double saldo = loteServicios.saldoProductor(idProductor);
		return Map.of("idProductor", idProductor, "saldoDisponible", saldo);
	}

	@GetMapping("/productores/{idProductor}/lotes")
	public Object historialProductor(@PathVariable Integer idProductor) {
		verificarPropietario(idProductor);
		return loteServicios.historialProductor(idProductor);
	}

	@GetMapping("/productores/{idProductor}/resumen")
	public Map<String, Object> resumenProductor(@PathVariable Integer idProductor) {
		verificarPropietario(idProductor);
		return reporteServicios.resumenProductor(idProductor);
	}

	private void verificarPropietario(Integer idUsuario) {
		if (!authenticatedUserService.getCurrentUser().getIdUsuario().equals(idUsuario)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo puedes consultar tu propia cuenta");
		}
	}
}
