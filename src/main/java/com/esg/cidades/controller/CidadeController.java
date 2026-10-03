package com.esg.cidades.controller;

import com.esg.cidades.model.Cidade;
import com.esg.cidades.service.CidadeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cidades")
public class CidadeController {

    private final CidadeService cidadeService;

    @Autowired
    public CidadeController(CidadeService cidadeService) {
        this.cidadeService = cidadeService;
    }

    @GetMapping
    public ResponseEntity<List<Cidade>> listarTodas(
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String nome) {

        if (uf != null && !uf.isBlank()) {
            return ResponseEntity.ok(cidadeService.buscarPorUf(uf));
        }
        if (nome != null && !nome.isBlank()) {
            return ResponseEntity.ok(cidadeService.buscarPorNome(nome));
        }
        return ResponseEntity.ok(cidadeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cidade> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cidadeService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cidade> criar(@Valid @RequestBody Cidade cidade) {
        Cidade novaCidade = cidadeService.criar(cidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaCidade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @Valid @RequestBody Cidade cidade) {
        return ResponseEntity.ok(cidadeService.atualizar(id, cidade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
