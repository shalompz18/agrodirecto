package com.upc.agrodirecto.security.service;

import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public CustomUserDetailsService(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if ("bloqueado".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            throw new UsernameNotFoundException("La cuenta está bloqueada");
        }

        String rol = usuario.getRol().getNombreRol();
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .roles(normalizarRol(rol))
                .build();
    }

    private String normalizarRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return "USUARIO";
        }
        return rol.trim().toUpperCase();
    }
}
