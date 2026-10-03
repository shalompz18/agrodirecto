package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.entidades.Cultivo;
import com.upc.agrodirecto.servicios.CultivoServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cultivos")
public class CultivoController {
	@Autowired
	private CultivoServicios cultivoServicios;

	@GetMapping
	public List<Cultivo> listar() {
		return cultivoServicios.listarCultivos();
	}
}
