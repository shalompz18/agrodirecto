package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "calificacion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Calificacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCalificacion;

	@ManyToOne
	@JoinColumn(name = "id_lote", nullable = false)
	private Lote lote;

	@ManyToOne
	@JoinColumn(name = "id_productor", nullable = false)
	private Usuario productor;

	private Short estrellas;
	private String comentario;
	private Boolean recomienda;
	private LocalDateTime fechaCalificacion;
}
