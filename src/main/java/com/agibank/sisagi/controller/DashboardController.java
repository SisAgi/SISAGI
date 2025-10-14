package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.dashboard.ContasPorGerenteResponse;
import com.agibank.sisagi.dto.dashboard.ContasPorMesResponse;
import com.agibank.sisagi.dto.dashboard.ContasPorTipoResponse;
import com.agibank.sisagi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/contas-mes")
    public ResponseEntity<ContasPorMesResponse> getContasNoMes() {
        return ResponseEntity.ok(dashboardService.getContasRegistradasNoMesAtual());
    }

    @GetMapping("/contas-por-gerente")
    public ResponseEntity<List<ContasPorGerenteResponse>> getContasPorGerente() {
        return ResponseEntity.ok(dashboardService.getContasPorGerente());
    }

    @GetMapping("/contas-por-tipo")
    public ResponseEntity<List<ContasPorTipoResponse>> getContasPorTipo() {
        return ResponseEntity.ok(dashboardService.getContasPorTipo());
    }
}
