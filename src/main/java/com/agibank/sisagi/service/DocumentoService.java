package com.agibank.sisagi.service;


import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.model.Documento;
import com.agibank.sisagi.repository.DocumentosRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.IOException;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoService {

    @Autowired
    private DocumentosRepository documentosRepository;

    public Documento salvar(MultipartFile arquivo) throws IOException {
        Documento documento = new Documento();

        documento.setNomeArquivo(arquivo.getOriginalFilename());
        documento.setTipoArquivo(arquivo.getContentType());
        documento.setDados(arquivo.getBytes());
        return documentosRepository.save(documento);
    }

    public Documento buscar(Long id){
        return documentosRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontrado("Documento não encontrado"));
    }
}
