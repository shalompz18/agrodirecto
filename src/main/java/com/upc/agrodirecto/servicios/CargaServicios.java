package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.PublicarCargaRequest;
import com.upc.agrodirecto.entidades.Carga;
import com.upc.agrodirecto.entidades.Cultivo;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.CargaRepositorio;
import com.upc.agrodirecto.repository.CultivoRepositorio;
import com.upc.agrodirecto.repository.LoteRepositorio;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CargaServicios {
	@Autowired
	private CargaRepositorio cargaRepositorio;
	@Autowired
	private CultivoRepositorio cultivoRepositorio;
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private LoteRepositorio loteRepositorio;

	public Carga publicarCarga(Integer idProductor, PublicarCargaRequest req) {
		Usuario productor = usuarioRepositorio.findById(idProductor).orElseThrow();
		Cultivo cultivo = cultivoRepositorio.findById(req.idCultivo()).orElseThrow();

		if (req.pesoKg() == null || req.pesoKg() <= 0) {
			throw new IllegalArgumentException("pesoKg debe ser mayor a 0");
		}
		if (req.tarifaPropuesta() == null || req.tarifaPropuesta() <= 0) {
			throw new IllegalArgumentException("tarifaPropuesta debe ser mayor a 0");
		}

		Carga carga = new Carga();
		carga.setCodigoCarga("C-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		carga.setProductor(productor);
		carga.setCultivo(cultivo);
		carga.setPesoKg(req.pesoKg());
		carga.setTipoCarga(req.tipoCarga());
		carga.setDireccionDestino(req.direccionDestino());
		carga.setTarifaPropuesta(req.tarifaPropuesta());
		carga.setFechaRecojo(req.fechaRecojo());
		carga.setEsUrgente(req.esUrgente() != null && req.esUrgente());
		carga.setEstadoCarga("publicada");
		carga.setFechaPublicacion(LocalDateTime.now());

		return cargaRepositorio.save(carga);
	}

	public Carga cancelarCarga(Integer idCarga, Integer idProductor) {
		Carga carga = cargaRepositorio.findById(idCarga).orElseThrow();
		if (!carga.getProductor().getIdUsuario().equals(idProductor)) {
			throw new org.springframework.security.access.AccessDeniedException("Solo el productor propietario puede cancelar esta carga");
		}
		if (!"publicada".equals(carga.getEstadoCarga()) || carga.getLote() != null) {
			throw new IllegalStateException("Solo se puede cancelar una carga publicada que aún no está asignada a un lote");
		}
		carga.setEstadoCarga("cancelada");
		return cargaRepositorio.save(carga);
	}

	public List<Carga> listarPorEstado(String estado) {
		return cargaRepositorio.listarPorEstado(estado);
	}

	public List<Carga> listarCargasDeRetorno() {
		return cargaRepositorio.listarCargasDeRetornoDisponibles();
	}

	public Carga asignarRetorno(Integer idCarga, Integer idLote, Integer idTransportista) {
		Carga carga = cargaRepositorio.findById(idCarga).orElseThrow();
		if (!"publicada".equals(carga.getEstadoCarga())) {
			throw new IllegalStateException("La carga ya no esta disponible para asignar");
		}
		Lote lote = loteRepositorio.findById(idLote).orElseThrow();
		if (lote.getTransportista() == null || !lote.getTransportista().getIdUsuario().equals(idTransportista)) {
			throw new org.springframework.security.access.AccessDeniedException("El lote no pertenece al transportista autenticado");
		}
		Usuario transportista = usuarioRepositorio.findById(idTransportista).orElseThrow();
		Double pesoActual = cargaRepositorio.sumarPesoPorLote(idLote);
		double pesoActualKg = pesoActual == null ? 0.0 : pesoActual;
		double pesoNuevaCargaKg = carga.getPesoKg() == null ? 0.0 : carga.getPesoKg();
		double capacidadKg = (transportista.getCapacidadToneladas() == null ? 0.0 : transportista.getCapacidadToneladas() * 1000.0);
		if (pesoActualKg + pesoNuevaCargaKg > capacidadKg) {
			throw new IllegalArgumentException("La carga supera la capacidad disponible del transportista");
		}
		carga.setLote(lote);
		carga.setEstadoCarga("confirmada_en_lote");
		return cargaRepositorio.save(carga);
	}
}
