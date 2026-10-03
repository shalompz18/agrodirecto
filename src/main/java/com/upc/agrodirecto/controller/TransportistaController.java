package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.servicios.LoteServicios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transportistas")
public class TransportistaController {
	@Autowired
	private LoteServicios loteServicios;

	@GetMapping("/{idTransportista}/lotes")
	public List<Lote> historial(@PathVariable Integer idTransportista, @RequestParam(required = false) String estado) {
		return loteServicios.historialTransportista(idTransportista, estado);
	}
}
