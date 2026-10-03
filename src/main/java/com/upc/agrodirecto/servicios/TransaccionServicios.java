package com.upc.agrodirecto.servicios;

import com.upc.agrodirecto.entidades.Lote;
import com.upc.agrodirecto.repository.LoteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.YearMonth;

@Service
public class TransaccionServicios {
	@Autowired
	private LoteRepositorio loteRepositorio;

	public List<Lote> listarTransacciones(String mes, String estadoPago) {
		YearMonth periodo;
		try { periodo = (mes == null || mes.isBlank()) ? YearMonth.now() : YearMonth.parse(mes); }
		catch (Exception ex) { throw new IllegalArgumentException("El parámetro mes debe tener formato YYYY-MM"); }
		return loteRepositorio.listarTransaccionesPorPeriodo(periodo.atDay(1).atStartOfDay(),
				periodo.plusMonths(1).atDay(1).atStartOfDay(), estadoPago);
	}
}
