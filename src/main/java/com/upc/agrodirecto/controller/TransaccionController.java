package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.servicios.TransaccionServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacciones")
public class TransaccionController {
	@Autowired
	private TransaccionServicios transaccionServicios;

	@GetMapping
	public List<Lote> listar(@RequestParam(required = false) String mes,
			@RequestParam(required = false) String estadoPago) {
		return transaccionServicios.listarTransacciones(mes, estadoPago);
	}
}
