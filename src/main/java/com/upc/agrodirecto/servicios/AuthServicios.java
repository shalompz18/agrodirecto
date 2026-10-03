package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.dto.RegistroProductorRequest;
import com.upc.agrodirecto.dto.RegistroTransportistaRequest;
import com.upc.agrodirecto.entidades.Rol;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.RolRepositorio;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServicios {
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;
	@Autowired
	private RolRepositorio rolRepositorio;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public Usuario guardarUsuario(Usuario usuario) {
		return usuarioRepositorio.save(usuario);
	}

	public Usuario buscarPorCorreo(String correo) {
		return usuarioRepositorio.findByCorreo(correo)
				.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
	}

	public Usuario registrarProductor(RegistroProductorRequest req) {
		if (usuarioRepositorio.countByCorreo(req.correo().trim().toLowerCase()) > 0) {
			throw new IllegalStateException("El correo ya esta registrado");
		}
		Rol rolProductor = rolRepositorio.findById(1).orElseThrow();

		Usuario usuario = new Usuario();
		usuario.setNombreCompleto(req.nombreCompleto());
		usuario.setCorreo(req.correo().trim().toLowerCase());
		usuario.setContrasena(passwordEncoder.encode(req.contrasena()));
		usuario.setRol(rolProductor);
		usuario.setEstadoCuenta("pendiente");
		usuario.setFechaRegistro(LocalDateTime.now());
		usuario.setDni(req.dni());
		usuario.setDistrito(req.distrito());

		return usuarioRepositorio.save(usuario);
	}

	public Usuario registrarTransportista(RegistroTransportistaRequest req) {
		if (usuarioRepositorio.countByCorreo(req.correo().trim().toLowerCase()) > 0) {
			throw new IllegalStateException("El correo ya esta registrado");
		}
		Rol rolTransportista = rolRepositorio.findById(2).orElseThrow();

		Usuario usuario = new Usuario();
		usuario.setNombreCompleto(req.nombreCompleto());
		usuario.setCorreo(req.correo().trim().toLowerCase());
		usuario.setContrasena(passwordEncoder.encode(req.contrasena()));
		usuario.setRol(rolTransportista);
		usuario.setEstadoCuenta("pendiente");
		usuario.setFechaRegistro(LocalDateTime.now());
		usuario.setRuc(req.ruc());
		usuario.setPlaca(req.placa());
		usuario.setCapacidadToneladas(req.capacidadToneladas());
		usuario.setLicencia(req.licencia());

		return usuarioRepositorio.save(usuario);
	}
}
