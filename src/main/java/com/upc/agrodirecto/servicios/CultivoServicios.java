package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.Cultivo;
import com.upc.agrodirecto.repository.CultivoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CultivoServicios {
	@Autowired
	private CultivoRepositorio cultivoRepositorio;

	public List<Cultivo> listarCultivos() {
		return cultivoRepositorio.listarOrdenados();
	}
}
