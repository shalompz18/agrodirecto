package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Carga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CargaRepositorio extends JpaRepository<Carga, Integer> {

	@Query("SELECT c FROM Carga c JOIN FETCH c.productor p JOIN FETCH c.cultivo cu " +
			"WHERE (:estado IS NULL OR c.estadoCarga = :estado) ORDER BY c.fechaPublicacion DESC")
	List<Carga> listarPorEstado(@Param("estado") String estado);

	@Query("SELECT c FROM Carga c JOIN FETCH c.cultivo cu " +
			"WHERE c.tipoCarga = 'seco' AND c.estadoCarga = 'publicada' AND c.lote IS NULL")
	List<Carga> listarCargasDeRetornoDisponibles();

	@Query("SELECT DISTINCT c FROM Carga c JOIN FETCH c.lote l " +
			"WHERE c.productor.idUsuario = :idProductor AND l.estadoLote = 'entregado' ORDER BY l.fechaLlegada DESC")
	List<Carga> listarCargasEntregadasPorProductor(@Param("idProductor") Integer idProductor);

	boolean existsByLote_IdLoteAndProductor_IdUsuario(Integer idLote, Integer idProductor);

	@Query("SELECT COALESCE(SUM(c.pesoKg), 0.0) FROM Carga c WHERE c.lote.idLote = :idLote AND c.estadoCarga <> 'cancelada'")
	Double sumarPesoPorLote(@Param("idLote") Integer idLote);

}
