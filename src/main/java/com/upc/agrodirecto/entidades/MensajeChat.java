package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje_chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MensajeChat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMensaje;

	@ManyToOne
	@JoinColumn(name = "id_solicitud", nullable = false)
	private SolicitudChat solicitud;

	@ManyToOne
	@JoinColumn(name = "id_usuario_emisor", nullable = false)
	private Usuario usuarioEmisor;

	private String contenido;
	private LocalDateTime fechaEnvio;
}
