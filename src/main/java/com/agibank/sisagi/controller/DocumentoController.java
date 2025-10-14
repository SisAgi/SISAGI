package com.agibank.sisagi.controller;



import com.agibank.sisagi.model.Documento;
import com.agibank.sisagi.service.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @PostMapping
    public ResponseEntity<Documento> upload(@RequestParam MultipartFile arquivo) throws IOException {
        Documento documento = documentoService.salvar(arquivo);
        return ResponseEntity.ok(documento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id){
        Documento documento = documentoService.buscar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNomeArquivo()+"\"")
                .contentType(MediaType.parseMediaType(documento.getTipoArquivo()))
                .body(documento.getDados());
    }

}
