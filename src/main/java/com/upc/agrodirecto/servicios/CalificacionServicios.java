package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.CalificacionRequest;
import com.upc.agrodirecto.entidades.Calificacion;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.CalificacionRepositorio;
import com.upc.agrodirecto.repository.CargaRepositorio;
import com.upc.agrodirecto.repository.LoteRepositorio;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalificacionServicios {
	@Autowired
	private CalificacionRepositorio calificacionRepositorio;
	@Autowired
	private CargaRepositorio cargaRepositorio;
	@Autowired
	private LoteRepositorio loteRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	public Calificacion calificar(CalificacionRequest req, Integer idProductor) {
		if (req.estrellas() == null || req.estrellas() < 1 || req.estrellas() > 5 || req.recomienda() == null) {
			throw new IllegalArgumentException("La calificación requiere entre 1 y 5 estrellas e indicar si recomienda");
		}
		if (calificacionRepositorio.countByLote_IdLoteAndProductor_IdUsuario(req.idLote(), idProductor) > 0) {
			throw new IllegalStateException("Este productor ya califico este lote");
		}
		Lote lote = loteRepositorio.findById(req.idLote()).orElseThrow();
		if (!"entregado".equals(lote.getEstadoLote())) {
			throw new IllegalStateException("Solo se puede calificar un lote entregado");
		}
		if (!cargaRepositorio.existsByLote_IdLoteAndProductor_IdUsuario(req.idLote(), idProductor)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo un productor asociado al lote puede calificarlo");
		}
		Usuario productor = usuarioRepositorio.findById(idProductor).orElseThrow();

		Calificacion calificacion = new Calificacion();
		calificacion.setLote(lote);
		calificacion.setProductor(productor);
		calificacion.setEstrellas(req.estrellas());
		calificacion.setComentario(req.comentario());
		calificacion.setRecomienda(req.recomienda());
		calificacion.setFechaCalificacion(LocalDateTime.now());

		return calificacionRepositorio.save(calificacion);
	}

	public List<Calificacion> listarPorLote(Integer idLote) {
		return calificacionRepositorio.listarPorLote(idLote);
	}
}
