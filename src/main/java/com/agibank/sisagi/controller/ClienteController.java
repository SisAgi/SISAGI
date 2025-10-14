package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.ClienteRequest;
import com.agibank.sisagi.dto.ClienteResponse;
import com.agibank.sisagi.dto.ClienteUpdateRequest;
import com.agibank.sisagi.dto.DocumentoDownloadDTO;
import com.agibank.sisagi.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final ClienteService clienteService;

    // Endpoint para a criação de um novo cliente
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestPart("cliente") ClienteRequest request, @RequestPart(value = "arquivo", required = false) MultipartFile arquivo) throws IOException {
        log.info("📥 Requisição recebida para criar cliente.");
        log.info("➡️ Nome: {}", request.nomeCompleto());
        log.info("➡️ Arquivo: {}",
                (arquivo != null && !arquivo.isEmpty()) ? arquivo.getOriginalFilename() : "Nenhum arquivo enviado");
        return ResponseEntity.ok(clienteService.criar(request, arquivo));

    }

    // Endpoint para listar todos os clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAllClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // Endpoint para buscar um cliente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/{id}/documento")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable Long id){
        log.info("⬇️ Requisição recebida para baixar documento do cliente ID: {}", id);

        DocumentoDownloadDTO downloadDTO = clienteService.buscarDocumentoPorClienteId(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDTO.tipoArquivo()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; file=\""+ downloadDTO.nomeArquivo()+ "\"")
                .body(downloadDTO.dados());
    }



    // Endpoint para atualizar os dados de um cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@Valid @PathVariable Long id, @RequestBody ClienteUpdateRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    // Endpoint para deletar um cliente pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
