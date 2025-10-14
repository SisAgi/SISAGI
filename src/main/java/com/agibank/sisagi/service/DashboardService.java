package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.dashboard.ContasPorGerenteResponse;
import com.agibank.sisagi.dto.dashboard.ContasPorMesResponse;
import com.agibank.sisagi.dto.dashboard.ContasPorTipoResponse;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    public ContasPorMesResponse getContasRegistradasNoMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.with(TemporalAdjusters.lastDayOfMonth());

        Long quantidade = contaRepository.countByDataAberturaBetween(inicioDoMes, fimDoMes);
        String mesAtual = hoje.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

        return new ContasPorMesResponse(mesAtual, quantidade);
    }

    public List<ContasPorGerenteResponse> getContasPorGerente() {
        return clienteRepository.countClientesByGerente();
    }

    public List<ContasPorTipoResponse> getContasPorTipo() {
        return contaRepository.countContasByTipo();
    }
}
