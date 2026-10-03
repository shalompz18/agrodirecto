package com.upc.agrodirecto.security.controller;

import com.upc.agrodirecto.dto.*;
import com.upc.agrodirecto.entidades.Usuario;
import com.upc.agrodirecto.security.dto.LoginRequest;
import com.upc.agrodirecto.security.dto.LoginResponse;
import com.upc.agrodirecto.security.util.JwtUtil;
import com.upc.agrodirecto.servicios.AuthServicios;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirements
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthServicios authServicios;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthServicios authServicios,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil) {
        this.authServicios = authServicios;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.correo(), req.contrasena()));

        Usuario usuario = authServicios.buscarPorCorreo(req.correo());
        usuario.setUltimoAcceso(java.time.LocalDateTime.now());
        authServicios.guardarUsuario(usuario);
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.correo());
        String rol = usuario.getRol().getNombreRol();
        String token = jwtUtil.generateToken(userDetails, usuario.getIdUsuario(), rol);

        return ResponseEntity.ok(new LoginResponse(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                rol,
                usuario.getEstadoCuenta()
        ));
    }

    @PostMapping("/registro-productor")
    public ResponseEntity<RegistroResponse> registrarProductor(@Valid @RequestBody RegistroProductorRequest req) {
        Usuario u = authServicios.registrarProductor(req);
        RegistroResponse resp = new RegistroResponse(u.getIdUsuario(), u.getCorreo(), u.getEstadoCuenta(), u.getRol().getIdRol());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/registro-transportista")
    public ResponseEntity<RegistroResponse> registrarTransportista(@Valid @RequestBody RegistroTransportistaRequest req) {
        Usuario u = authServicios.registrarTransportista(req);
        RegistroResponse resp = new RegistroResponse(u.getIdUsuario(), u.getCorreo(), u.getEstadoCuenta(), u.getRol().getIdRol());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
