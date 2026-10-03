package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.entidades.Notificacion;
import com.upc.agrodirecto.servicios.NotificacionServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {
	@Autowired
	private NotificacionServicios notificacionServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@GetMapping("/usuario/{idUsuario}")
	public Map<String, Object> listar(@PathVariable Integer idUsuario) {
		verificarPropietario(idUsuario);
		List<Notificacion> notificaciones = notificacionServicios.listarPorUsuario(idUsuario);
		long noLeidas = notificacionServicios.contarNoLeidas(idUsuario);
		return Map.of("totalNoLeidas", noLeidas, "notificaciones", notificaciones);
	}

	@PatchMapping("/{idNotificacion}/leer")
	public Notificacion marcarLeida(@PathVariable Integer idNotificacion) {
		return notificacionServicios.marcarLeida(idNotificacion, authenticatedUserService.getCurrentUser().getIdUsuario());
	}

	private void verificarPropietario(Integer idUsuario) {
		if (!authenticatedUserService.getCurrentUser().getIdUsuario().equals(idUsuario)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo puedes consultar tus notificaciones");
		}
	}
}
