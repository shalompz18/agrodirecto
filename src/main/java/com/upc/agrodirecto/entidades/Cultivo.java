package com.upc.agrodirecto.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cultivo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cultivo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCultivo;
	private String nombreCultivo;
}
