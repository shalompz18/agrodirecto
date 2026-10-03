package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.ConfirmarEntregaRequest;
import com.upc.agrodirecto.dto.ReportarRetrasoRequest;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.repository.LoteRepositorio;
import com.upc.agrodirecto.repository.CargaRepositorio;
import com.upc.agrodirecto.entidades.Carga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoteServicios {
	@Autowired
	private LoteRepositorio loteRepositorio;
	@Autowired
	private CargaRepositorio cargaRepositorio;

	public List<Lote> historialTransportista(Integer idTransportista, String filtro) {
		return loteRepositorio.listarPorTransportista(idTransportista, filtro);
	}

	public Lote detalleConCargas(Integer idLote) {
		return loteRepositorio.buscarDetalleConCargas(idLote).orElseThrow();
	}

	@Transactional
	public Lote confirmarEntrega(Integer idLote, ConfirmarEntregaRequest req, Integer idTransportistaAutenticado) {
		Lote lote = loteRepositorio.buscarDetalleConCargas(idLote).orElseThrow();
		if (lote.getTransportista() == null || !lote.getTransportista().getIdUsuario().equals(idTransportistaAutenticado)) {
			throw new org.springframework.security.access.AccessDeniedException("El lote no pertenece al transportista autenticado");
		}
		if (req.codigoValidacion() == null || !req.codigoValidacion().trim().equalsIgnoreCase(lote.getCodigoLote())) {
			throw new IllegalArgumentException("El código QR/manual no coincide con el código del lote");
		}
		if (!List.of("en_transito", "con_retraso").contains(lote.getEstadoLote())) {
			throw new IllegalStateException("El lote no esta en transito, no se puede confirmar entrega");
		}
		lote.setEstadoLote("entregado");
		lote.setFechaLlegada(req.fechaLlegada() != null ? req.fechaLlegada() : LocalDateTime.now());
		Lote guardado = loteRepositorio.save(lote);
		for (Carga carga : lote.getCargas() == null ? List.<Carga>of() : lote.getCargas()) {
			carga.setEstadoCarga("entregada");
			cargaRepositorio.save(carga);
		}
		return guardado;
	}

	public Lote reportarRetraso(Integer idLote, ReportarRetrasoRequest req, Integer idTransportistaAutenticado) {
		Lote lote = loteRepositorio.findById(idLote).orElseThrow();
		if (lote.getTransportista() == null || !lote.getTransportista().getIdUsuario().equals(idTransportistaAutenticado)) {
			throw new org.springframework.security.access.AccessDeniedException("El lote no pertenece al transportista autenticado");
		}
		if (!"en_transito".equals(lote.getEstadoLote())) {
			throw new IllegalStateException("El lote no esta en transito");
		}
		lote.setEstadoLote("con_retraso");
		return loteRepositorio.save(lote);
	}

	public Lote seguimiento(Integer idLote) {
		return loteRepositorio.findById(idLote).orElseThrow();
	}

	public List<Lote> historialProductor(Integer idProductor) {
		return loteRepositorio.listarLotesEntregadosDeProductor(idProductor);
	}

	public Double saldoProductor(Integer idProductor) {
		return loteRepositorio.calcularSaldoProductor(idProductor);
	}
}
