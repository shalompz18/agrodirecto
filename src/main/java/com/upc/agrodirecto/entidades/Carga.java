package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "carga")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Carga {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCarga;

	private String codigoCarga;

	@ManyToOne
	@JoinColumn(name = "id_productor", nullable = false)
	private Usuario productor;

	@ManyToOne
	@JoinColumn(name = "id_cultivo", nullable = false)
	private Cultivo cultivo;

	@ManyToOne
	@JoinColumn(name = "id_lote")
	@JsonIgnore
	private Lote lote;

	private Double pesoKg;
	private String tipoCarga; // perecible | seco
	private String direccionDestino;
	private Double tarifaPropuesta;
	private LocalDate fechaRecojo;
	private Boolean esUrgente;
	private String estadoCarga; // publicada | en_revision | confirmada_en_lote | en_transito | entregada | cancelada
	private LocalDateTime fechaPublicacion;
}
