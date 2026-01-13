package com.totaldocs.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.modelo.Layout;
import com.totaldocs.repository.LayoutRepository;

@RestController
@RequestMapping("/api/layouts")
public class LayoutControleAPI {

    @Autowired
    private LayoutRepository layoutRepository;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadLayout(@PathVariable Integer id) {

        Layout layout = layoutRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Layout não encontrado id " + id)
            );

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + layout.getNomeLayout() + "\""
            )
            .contentType(MediaType.parseMediaType(layout.getTipoMIME()))
            .body(layout.getConteudoLayout());
    }
}
