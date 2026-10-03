package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudChat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idSolicitud;

	@ManyToOne
	@JoinColumn(name = "id_productor", nullable = false)
	private Usuario productor;

	@ManyToOne
	@JoinColumn(name = "id_transportista", nullable = false)
	private Usuario transportista;

	@ManyToOne
	@JoinColumn(name = "id_lote", nullable = false)
	private Lote lote;

	@ManyToOne
	@JoinColumn(name = "id_administrador")
	private Usuario administrador;

	private String estadoSolicitud; // pendiente | aprobado | rechazado
	private LocalDateTime fechaSolicitud;
}
