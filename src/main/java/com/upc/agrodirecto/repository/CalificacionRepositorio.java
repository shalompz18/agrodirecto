package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalificacionRepositorio extends JpaRepository<Calificacion, Integer> {

	long countByLote_IdLoteAndProductor_IdUsuario(Integer idLote, Integer idProductor);

	@Query("SELECT cal FROM Calificacion cal JOIN FETCH cal.productor p WHERE cal.lote.idLote = :idLote")
	List<Calificacion> listarPorLote(@Param("idLote") Integer idLote);

	@Query("SELECT cal FROM Calificacion cal JOIN FETCH cal.productor p JOIN FETCH cal.lote l " +
			"WHERE l.transportista.idUsuario = :idTransportista ORDER BY cal.fechaCalificacion DESC")
	List<Calificacion> listarPorTransportista(@Param("idTransportista") Integer idTransportista);

}
