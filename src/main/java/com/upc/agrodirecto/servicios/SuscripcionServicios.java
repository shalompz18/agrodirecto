package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SuscripcionServicios {
	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	public Usuario contratarPremium(Integer idUsuario) {
		Usuario usuario = usuarioRepositorio.findById(idUsuario).orElseThrow();
		// Si ya es premium y sigue vigente, los 30 dias se suman desde su vigencia actual;
		// si no, se cuentan desde hoy.
		LocalDateTime base = (usuario.getPremiumHasta() != null && usuario.getPremiumHasta().isAfter(LocalDateTime.now()))
				? usuario.getPremiumHasta()
				: LocalDateTime.now();
		usuario.setPremiumHasta(base.plusDays(30));
		return usuarioRepositorio.save(usuario);
	}

	public boolean esPremium(Usuario usuario) {
		return usuario.getPremiumHasta() != null && usuario.getPremiumHasta().isAfter(LocalDateTime.now());
	}
}
