package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface LoteRepositorio extends JpaRepository<Lote, Integer> {

	@Query("SELECT l FROM Lote l WHERE l.transportista.idUsuario = :idTransportista " +
			"AND (:filtro IS NULL " +
			"     OR (:filtro = 'activos' AND l.estadoLote IN ('en_transito', 'con_retraso')) " +
			"     OR (:filtro = 'completados' AND l.estadoLote = 'entregado')) " +
			"ORDER BY l.fechaSalida DESC")
	List<Lote> listarPorTransportista(@Param("idTransportista") Integer idTransportista, @Param("filtro") String filtro);

	@Query("SELECT l FROM Lote l LEFT JOIN FETCH l.cargas c LEFT JOIN FETCH c.productor p LEFT JOIN FETCH c.cultivo cu " +
			"WHERE l.idLote = :idLote")
	Optional<Lote> buscarDetalleConCargas(@Param("idLote") Integer idLote);

	@Query("SELECT l FROM Lote l WHERE l.estadoPago IS NOT NULL " +
			"AND (:estadoPago IS NULL OR l.estadoPago = :estadoPago) " +
			"ORDER BY l.fechaTransaccion DESC")
	List<Lote> listarTransacciones(@Param("estadoPago") String estadoPago);

	@Query("SELECT DISTINCT l FROM Lote l JOIN l.cargas c " +
			"WHERE c.productor.idUsuario = :idProductor AND l.estadoLote = 'entregado' " +
			"ORDER BY l.fechaSalida DESC")
	List<Lote> listarLotesEntregadosDeProductor(@Param("idProductor") Integer idProductor);

	@Query("SELECT COALESCE(SUM(l.montoNeto), 0.0) FROM Lote l JOIN l.cargas c " +
			"WHERE c.productor.idUsuario = :idProductor AND l.estadoPago = 'pagado'")
	Double calcularSaldoProductor(@Param("idProductor") Integer idProductor);

	@Query("SELECT l FROM Lote l WHERE l.estadoPago IS NOT NULL " +
			"AND l.fechaTransaccion >= :inicio AND l.fechaTransaccion < :fin " +
			"AND (:estadoPago IS NULL OR l.estadoPago = :estadoPago) ORDER BY l.fechaTransaccion DESC")
	List<Lote> listarTransaccionesPorPeriodo(@Param("inicio") LocalDateTime inicio,
			@Param("fin") LocalDateTime fin, @Param("estadoPago") String estadoPago);

	@Query("SELECT COALESCE(SUM(l.fleteTotal), 0.0), COALESCE(SUM(l.montoComision), 0.0) " +
			"FROM Lote l WHERE l.estadoPago IS NOT NULL AND l.fechaTransaccion >= :inicio AND l.fechaTransaccion < :fin")
	Object[] resumirTransaccionesPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("SELECT DISTINCT l FROM Lote l JOIN l.cargas c " +
			"WHERE c.productor.idUsuario = :idProductor AND l.estadoLote = 'entregado' " +
			"AND l.fechaLlegada >= :inicio AND l.fechaLlegada < :fin ORDER BY l.fechaLlegada DESC")
	List<Lote> listarLotesEntregadosDeProductorEnPeriodo(@Param("idProductor") Integer idProductor,
			@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
