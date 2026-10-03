package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.Calificacion;
import com.upc.agrodirecto.entidades.Carga;
import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.repository.CalificacionRepositorio;
import com.upc.agrodirecto.repository.CargaRepositorio;
import com.upc.agrodirecto.repository.LoteRepositorio;
import com.upc.agrodirecto.repository.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class ReporteServicios {
    private final LoteRepositorio loteRepositorio;
    private final CargaRepositorio cargaRepositorio;
    private final CalificacionRepositorio calificacionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ReporteServicios(LoteRepositorio loteRepositorio, CargaRepositorio cargaRepositorio,
                            CalificacionRepositorio calificacionRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.loteRepositorio = loteRepositorio;
        this.cargaRepositorio = cargaRepositorio;
        this.calificacionRepositorio = calificacionRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    private YearMonth parseMes(String mes) {
        if (mes == null || mes.isBlank()) return YearMonth.now();
        try { return YearMonth.parse(mes); }
        catch (Exception ex) { throw new IllegalArgumentException("El parámetro mes debe tener formato YYYY-MM"); }
    }

    public Map<String, Object> resumenTransacciones(String mes) {
        YearMonth periodo = parseMes(mes);
        Object[] resultado = loteRepositorio.resumirTransaccionesPorPeriodo(
                periodo.atDay(1).atStartOfDay(), periodo.plusMonths(1).atDay(1).atStartOfDay());
        double total = resultado == null || resultado[0] == null ? 0.0 : ((Number) resultado[0]).doubleValue();
        double comision = resultado == null || resultado[1] == null ? 0.0 : ((Number) resultado[1]).doubleValue();
        return Map.of("mes", periodo.toString(), "totalTransado", total, "comisionAcumulada", comision);
    }

    public Map<String, Object> ahorroProductor(Integer idProductor) {
        List<Carga> cargas = cargaRepositorio.listarCargasEntregadasPorProductor(idProductor);
        double ahorroTemporada = 0.0;
        double ahorroMes = 0.0;
        Set<Integer> lotesContados = new HashSet<>();
        YearMonth mesActual = YearMonth.now();
        for (Carga carga : cargas) {
            Lote lote = carga.getLote();
            if (lote == null || lote.getFleteTotal() == null || carga.getTarifaPropuesta() == null) continue;
            Double pesoTotal = cargaRepositorio.sumarPesoPorLote(lote.getIdLote());
            if (pesoTotal == null || pesoTotal <= 0 || carga.getPesoKg() == null) continue;
            // Estimación: tarifa propuesta como referencia de viaje individual menos la parte
            // proporcional del flete consolidado asignada por peso de la carga.
            double costoCompartido = lote.getFleteTotal() * (carga.getPesoKg() / pesoTotal);
            double ahorro = Math.max(0.0, carga.getTarifaPropuesta() - costoCompartido);
            ahorroTemporada += ahorro;
            if (lote.getFechaLlegada() != null && YearMonth.from(lote.getFechaLlegada()).equals(mesActual)) ahorroMes += ahorro;
            lotesContados.add(lote.getIdLote());
        }
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("ahorroTemporada", redondear(ahorroTemporada));
        respuesta.put("ahorroMesActual", redondear(ahorroMes));
        respuesta.put("totalViajes", (long) lotesContados.size());
        return respuesta;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> reputacionTransportista(Integer idTransportista) {
        usuarioRepositorio.buscarTransportistaPorId(idTransportista).orElseThrow();
        List<Calificacion> calificaciones = calificacionRepositorio.listarPorTransportista(idTransportista);
        double promedio = calificaciones.stream().map(Calificacion::getEstrellas).filter(Objects::nonNull)
                .mapToInt(Short::intValue).average().orElse(0.0);
        List<Map<String, Object>> comentarios = calificaciones.stream().limit(10).map(cal -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productor", cal.getProductor() == null ? "Usuario" : cal.getProductor().getNombreCompleto());
            item.put("estrellas", cal.getEstrellas());
            item.put("comentario", cal.getComentario());
            return item;
        }).toList();
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("promedioEstrellas", calificaciones.isEmpty() ? null : redondear(promedio));
        respuesta.put("totalResenas", (long) calificaciones.size());
        respuesta.put("comentarios", comentarios);
        return respuesta;
    }

    public Map<String, Object> resumenProductor(Integer idProductor) {
        YearMonth periodo = YearMonth.now();
        List<Lote> lotes = loteRepositorio.listarLotesEntregadosDeProductorEnPeriodo(
                idProductor, periodo.atDay(1).atStartOfDay(), periodo.plusMonths(1).atDay(1).atStartOfDay());
        double neto = lotes.stream().map(Lote::getMontoNeto).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
        return Map.of("totalLotesCompletados", (long) lotes.size(), "montoNetoAcumulado", redondear(neto));
    }

    private double redondear(double valor) { return Math.round(valor * 100.0) / 100.0; }
}
