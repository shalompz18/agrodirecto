package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.SolicitudChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolicitudChatRepositorio extends JpaRepository<SolicitudChat, Integer> {

	@Query("SELECT sc FROM SolicitudChat sc JOIN FETCH sc.productor p JOIN FETCH sc.transportista t JOIN FETCH sc.lote l " +
			"WHERE (:estado IS NULL OR sc.estadoSolicitud = :estado)")
	List<SolicitudChat> listarPorEstado(@Param("estado") String estado);
}
