package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.Notificacion;
import com.upc.agrodirecto.repository.NotificacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionServicios {
	@Autowired
	private NotificacionRepositorio notificacionRepositorio;

	public List<Notificacion> listarPorUsuario(Integer idUsuario) {
		return notificacionRepositorio.listarPorUsuario(idUsuario);
	}

	public long contarNoLeidas(Integer idUsuario) {
		return notificacionRepositorio.countByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
	}

	public Notificacion marcarLeida(Integer idNotificacion, Integer idUsuarioAutenticado) {
		Notificacion notificacion = notificacionRepositorio.findById(idNotificacion).orElseThrow();
		if (!notificacion.getUsuario().getIdUsuario().equals(idUsuarioAutenticado)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo puedes marcar tus notificaciones");
		}
		notificacion.setLeida(true);
		return notificacionRepositorio.save(notificacion);
	}
}
