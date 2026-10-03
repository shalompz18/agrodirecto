package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.dto.EstadoSolicitudRequest;
import com.upc.agrodirecto.dto.MensajeChatRequest;
import com.upc.agrodirecto.entidades.MensajeChat;
import com.upc.agrodirecto.entidades.SolicitudChat;
import com.upc.agrodirecto.servicios.ChatServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats/solicitudes")
public class ChatController {
	@Autowired
	private ChatServicios chatServicios;
	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	@GetMapping
	public List<SolicitudChat> listar(@RequestParam(required = false) String estado) {
		return chatServicios.listarSolicitudes(estado);
	}

	@PutMapping("/{idSolicitud}/estado")
	public SolicitudChat moderar(@PathVariable Integer idSolicitud, @RequestBody EstadoSolicitudRequest req) {
		return chatServicios.moderarSolicitud(idSolicitud, req, authenticatedUserService.getCurrentUser().getIdUsuario());
	}

	@PostMapping("/{idSolicitud}/mensajes")
	public ResponseEntity<MensajeChat> enviarMensaje(@PathVariable Integer idSolicitud, @RequestBody MensajeChatRequest req) {
		MensajeChat mensaje = chatServicios.enviarMensaje(idSolicitud, req, authenticatedUserService.getCurrentUser().getIdUsuario());
		return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
	}
}
