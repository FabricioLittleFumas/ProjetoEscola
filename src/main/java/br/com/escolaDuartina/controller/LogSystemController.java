package br.com.escolaDuartina.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.escolaDuartina.dto.LogSystemResposta;
import br.com.escolaDuartina.dto.PaginaResposta;
import br.com.escolaDuartina.service.LogSystemService;

@RestController
@RequestMapping("/logs")
@CrossOrigin
public class LogSystemController {
	private final LogSystemService logService;

	public LogSystemController(LogSystemService logService) {
		this.logService = logService;
	}

	@GetMapping
	public ResponseEntity<PaginaResposta<LogSystemResposta>> listar(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "acao", defaultValue = "") String acao) {
		int paginaValida = Math.max(0, page);
		int tamanhoValido = Math.min(Math.max(1, size), 100);
		Page<LogSystemResposta> resultado = logService.listar(acao, PageRequest.of(paginaValida, tamanhoValido));
		PaginaResposta<LogSystemResposta> resposta = new PaginaResposta<>(resultado.getContent(), resultado.getNumber(),
				resultado.getSize(), resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(),
				resultado.isLast());
		return ResponseEntity.ok(resposta);
	}
}
