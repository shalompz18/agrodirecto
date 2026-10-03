package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.GastoViajeRequest;
import com.upc.agrodirecto.entidades.GastoViaje;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.repository.GastoViajeRepositorio;
import com.upc.agrodirecto.repository.LoteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GastoViajeServicios {
	@Autowired
	private GastoViajeRepositorio gastoViajeRepositorio;
	@Autowired
	private LoteRepositorio loteRepositorio;

	public GastoViaje registrarGasto(Integer idLote, GastoViajeRequest req, Integer idTransportista) {
		if (req.monto() == null || req.monto() <= 0) {
			throw new IllegalArgumentException("monto debe ser mayor a 0");
		}
		Lote lote = loteRepositorio.findById(idLote).orElseThrow();
		if (lote.getTransportista() == null || !lote.getTransportista().getIdUsuario().equals(idTransportista)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo el transportista asignado puede registrar gastos");
		}

		GastoViaje gasto = new GastoViaje();
		gasto.setLote(lote);
		gasto.setConcepto(req.concepto());
		gasto.setMonto(req.monto());
		gasto.setFechaHora(LocalDateTime.now());
		return gastoViajeRepositorio.save(gasto);
	}

	public List<GastoViaje> listarPorLote(Integer idLote, Integer idTransportista) {
		verificarTransportista(idLote, idTransportista);
		return gastoViajeRepositorio.listarPorLote(idLote);
	}

	public double totalGastos(Integer idLote, Integer idTransportista) {
		return listarPorLote(idLote, idTransportista).stream().mapToDouble(GastoViaje::getMonto).sum();
	}

	private void verificarTransportista(Integer idLote, Integer idTransportista) {
		Lote lote = loteRepositorio.findById(idLote).orElseThrow();
		if (lote.getTransportista() == null || !lote.getTransportista().getIdUsuario().equals(idTransportista)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo el transportista asignado puede consultar gastos");
		}
	}
}
