package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lote")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idLote;

	private String codigoLote;

	@ManyToOne
	@JoinColumn(name = "id_transportista")
	private Usuario transportista;

	private String estadoLote; // formandose | confirmado | en_transito | con_retraso | entregado | cancelado
	private LocalDateTime fechaCreacion;

	// Datos del viaje (antes tabla viaje)
	private String origen;
	private String destino;
	private LocalDateTime fechaSalida;
	private LocalDateTime fechaLlegada;
	private Double fleteTotal;

	// Datos de la transaccion (antes tabla transaccion)
	private Double porcentajeComision;
	private Double montoComision;
	private Double montoNeto;
	private String estadoPago; // pendiente | pagado
	private LocalDateTime fechaTransaccion;

	// Necesario para EP21 (detalle de lote consolidado): LEFT JOIN FETCH l.cargas
	@OneToMany(mappedBy = "lote")
	private List<Carga> cargas;
}
