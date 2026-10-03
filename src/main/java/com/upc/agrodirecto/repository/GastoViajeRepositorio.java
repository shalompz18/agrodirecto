package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.GastoViaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GastoViajeRepositorio extends JpaRepository<GastoViaje, Integer> {

	@Query("SELECT g FROM GastoViaje g WHERE g.lote.idLote = :idLote ORDER BY g.fechaHora ASC")
	List<GastoViaje> listarPorLote(@Param("idLote") Integer idLote);
}
