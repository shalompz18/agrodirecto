package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "gasto_viaje")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GastoViaje {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idGasto;

	@ManyToOne
	@JoinColumn(name = "id_lote", nullable = false)
	private Lote lote;

	private String concepto;
	private Double monto;
	private LocalDateTime fechaHora;
}
