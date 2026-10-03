package com.upc.agrodirecto.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idUsuario;

	private String nombreCompleto;
	private String correo;
	@JsonIgnore
	private String contrasena;

	@ManyToOne
	@JoinColumn(name = "id_rol", nullable = false)
	private Rol rol;

	private String estadoCuenta; // pendiente | verificado | bloqueado
	private LocalDateTime fechaRegistro;

	// Datos de productor (HU-01)
	private String dni;
	private String distrito;

	// Datos de transportista (HU-01)
	private String ruc;
	private String placa;
	private Double capacidadToneladas;
	private String licencia;

	// Datos de administrador (HU-16)
	private LocalDateTime ultimoAcceso;

	// Plan premium (HU-21)
	private LocalDateTime premiumHasta;
}
