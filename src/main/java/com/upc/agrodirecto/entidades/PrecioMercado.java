package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "precio_mercado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrecioMercado {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPrecio;

	@ManyToOne
	@JoinColumn(name = "id_cultivo", nullable = false)
	private Cultivo cultivo;

	private Double precioPorKg;
	private LocalDate fechaPrecio;
}
