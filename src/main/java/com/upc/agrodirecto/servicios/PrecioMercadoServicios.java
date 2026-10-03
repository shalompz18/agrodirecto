package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.PrecioMercado;
import com.upc.agrodirecto.repository.PrecioMercadoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrecioMercadoServicios {
	@Autowired
	private PrecioMercadoRepositorio precioMercadoRepositorio;

	public List<PrecioMercado> listarPorFecha(LocalDate fecha) {
		LocalDate consulta = (fecha != null) ? fecha : LocalDate.now();
		return precioMercadoRepositorio.listarPorFecha(consulta);
	}

	public PrecioMercado ultimoPrecio(Integer idCultivo) {
		List<PrecioMercado> lista = precioMercadoRepositorio.listarPorCultivoOrdenado(idCultivo);
		if (lista.isEmpty()) {
			throw new IllegalStateException("El cultivo no tiene precios registrados");
		}
		return lista.get(0);
	}
}
