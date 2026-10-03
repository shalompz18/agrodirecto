package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Integer> {

	@Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :idUsuario ORDER BY n.fechaCreacion DESC")
	List<Notificacion> listarPorUsuario(@Param("idUsuario") Integer idUsuario);

	long countByUsuario_IdUsuarioAndLeidaFalse(Integer idUsuario);
}
