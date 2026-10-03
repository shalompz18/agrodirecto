package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CultivoRepositorio extends JpaRepository<Cultivo, Integer> {

	@Query("SELECT c FROM Cultivo c ORDER BY c.nombreCultivo ASC")
	List<Cultivo> listarOrdenados();
}
