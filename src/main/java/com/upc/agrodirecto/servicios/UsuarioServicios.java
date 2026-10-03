package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.EstadoUsuarioRequest;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioServicios {
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	private static final Set<String> ESTADOS_VALIDOS = Set.of("verificado", "bloqueado");

	public List<Usuario> listarPorRol(Integer idRol) {
		return usuarioRepositorio.listarPorRol(idRol);
	}

	public Usuario cambiarEstado(Integer idUsuario, EstadoUsuarioRequest req) {
		if (!ESTADOS_VALIDOS.contains(req.estadoCuenta())) {
			throw new IllegalArgumentException("estadoCuenta invalido");
		}
		Usuario usuario = usuarioRepositorio.findById(idUsuario).orElseThrow();
		usuario.setEstadoCuenta(req.estadoCuenta());
		return usuarioRepositorio.save(usuario);
	}

	public Usuario perfilTransportista(Integer idUsuario) {
		return usuarioRepositorio.buscarTransportistaPorId(idUsuario).orElseThrow();
	}

	public Usuario perfilAdministrador(Integer idUsuario) {
		return usuarioRepositorio.buscarAdministradorPorId(idUsuario).orElseThrow();
	}
}
