package br.com.escolaDuartina.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.com.escolaDuartina.service.VozNeuralService;
import br.com.escolaDuartina.excecao.PdfLeituraException;

@RestController
@RequestMapping("/leitura-pdf/voz")
@PreAuthorize("isAuthenticated()")
public class VozNeuralController {
    private final VozNeuralService service;

    public VozNeuralController(VozNeuralService service) { this.service = service; }

    public record Voz(String id, String nome, String idioma) {}
    public record Pedido(String texto, String voz) {}

    @GetMapping
    public List<Voz> vozes() {
        return List.of(new Voz("br-dora", "Dora · Kokoro", "pt-BR"),
                new Voz("br-alex", "Alex · Kokoro", "pt-BR"),
                new Voz("br-santa", "Santa · Kokoro", "pt-BR"));
    }

    @PostMapping(produces = "audio/wav")
    public ResponseEntity<byte[]> gerar(@RequestBody Pedido pedido) {
        if (pedido.texto() == null || pedido.texto().isBlank() || pedido.texto().length() > 1500
                || pedido.voz() == null
                || !List.of("br-dora", "br-alex", "br-santa", "br-faber", "pt-tugao").contains(pedido.voz())) {
            throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "Informe uma voz válida e um trecho de até 1.500 caracteres.");
        }
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(service.gerar(pedido.texto(), pedido.voz()));
    }
}
