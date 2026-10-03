package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.EstadoSolicitudRequest;
import com.upc.agrodirecto.dto.MensajeChatRequest;
import com.upc.agrodirecto.entidades.MensajeChat;
import com.upc.agrodirecto.entidades.SolicitudChat;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.MensajeChatRepositorio;
import com.upc.agrodirecto.repository.SolicitudChatRepositorio;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServicios {
	@Autowired
	private SolicitudChatRepositorio solicitudChatRepositorio;
	@Autowired
	private MensajeChatRepositorio mensajeChatRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	public List<SolicitudChat> listarSolicitudes(String estado) {
		return solicitudChatRepositorio.listarPorEstado(estado);
	}

	public SolicitudChat moderarSolicitud(Integer idSolicitud, EstadoSolicitudRequest req, Integer idAdministradorAutenticado) {
		SolicitudChat solicitud = solicitudChatRepositorio.findById(idSolicitud).orElseThrow();
		if (!"pendiente".equals(solicitud.getEstadoSolicitud())) {
			throw new IllegalStateException("La solicitud ya fue moderada");
		}
		Usuario administrador = usuarioRepositorio.findById(idAdministradorAutenticado).orElseThrow();
		if (administrador.getRol() == null || administrador.getRol().getIdRol() != 3) {
			throw new org.springframework.security.access.AccessDeniedException("Solo un administrador puede moderar solicitudes");
		}
		if (!List.of("aprobado", "rechazado").contains(req.estadoSolicitud())) throw new IllegalArgumentException("Estado de solicitud inválido");
		solicitud.setEstadoSolicitud(req.estadoSolicitud());
		solicitud.setAdministrador(administrador);
		return solicitudChatRepositorio.save(solicitud);
	}

	public MensajeChat enviarMensaje(Integer idSolicitud, MensajeChatRequest req, Integer idEmisorAutenticado) {
		SolicitudChat solicitud = solicitudChatRepositorio.findById(idSolicitud).orElseThrow();
		if (!"aprobado".equals(solicitud.getEstadoSolicitud())) {
			throw new org.springframework.security.access.AccessDeniedException("El chat no está aprobado todavía");
		}
		Usuario emisor = usuarioRepositorio.findById(idEmisorAutenticado).orElseThrow();
		boolean participante = solicitud.getProductor().getIdUsuario().equals(idEmisorAutenticado)
				|| solicitud.getTransportista().getIdUsuario().equals(idEmisorAutenticado);
		if (!participante) throw new org.springframework.security.access.AccessDeniedException("Solo las partes del chat pueden enviar mensajes");
		if (req.contenido() == null || req.contenido().isBlank() || req.contenido().length() > 500) {
			throw new IllegalArgumentException("El contenido debe tener entre 1 y 500 caracteres");
		}

		MensajeChat mensaje = new MensajeChat();
		mensaje.setSolicitud(solicitud);
		mensaje.setUsuarioEmisor(emisor);
		mensaje.setContenido(req.contenido());
		mensaje.setFechaEnvio(LocalDateTime.now());
		return mensajeChatRepositorio.save(mensaje);
	}
}
