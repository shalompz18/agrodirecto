package com.upc.agrodirecto.repository;

import com.upc.agrodirecto.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

	long countByCorreo(String correo);

	// Lo usan AuthServicios, AuthenticatedUserService y CustomUserDetailsService
	// (login, "quien soy" y la validacion de Spring Security respectivamente).
	Optional<Usuario> findByCorreo(String correo);

	@Query("SELECT u FROM Usuario u JOIN FETCH u.rol r WHERE (:idRol IS NULL OR r.idRol = :idRol) ORDER BY u.fechaRegistro DESC")
	List<Usuario> listarPorRol(@Param("idRol") Integer idRol);

	@Query("SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario AND u.rol.idRol = 2")
	Optional<Usuario> buscarTransportistaPorId(@Param("idUsuario") Integer idUsuario);

	@Query("SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario AND u.rol.idRol = 3")
	Optional<Usuario> buscarAdministradorPorId(@Param("idUsuario") Integer idUsuario);
}
