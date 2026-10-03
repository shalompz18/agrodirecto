package com.upc.agrodirecto.controller;

import com.upc.agrodirecto.servicios.ReporteServicios;
import com.upc.agrodirecto.security.service.AuthenticatedUserService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {
    private final ReporteServicios reporteServicios;
    private final AuthenticatedUserService authenticatedUserService;
    public ReporteController(ReporteServicios reporteServicios, AuthenticatedUserService authenticatedUserService) { this.reporteServicios = reporteServicios; this.authenticatedUserService = authenticatedUserService; }

    @GetMapping("/transacciones/resumen")
    public Map<String, Object> resumenTransacciones(@RequestParam(required = false) String mes) {
        return reporteServicios.resumenTransacciones(mes);
    }

    @GetMapping("/ahorro-productor/{idProductor}")
    public Map<String, Object> ahorroProductor(@PathVariable Integer idProductor) {
        verificarPropietario(idProductor);
        return reporteServicios.ahorroProductor(idProductor);
    }

    @GetMapping("/reputacion-transportista/{idTransportista}")
    public Map<String, Object> reputacionTransportista(@PathVariable Integer idTransportista) {
        return reporteServicios.reputacionTransportista(idTransportista);
    }
    private void verificarPropietario(Integer idUsuario) {
        if (!authenticatedUserService.getCurrentUser().getIdUsuario().equals(idUsuario)) {
            throw new org.springframework.security.access.AccessDeniedException("Solo puedes consultar tus propios reportes");
        }
    }
}
