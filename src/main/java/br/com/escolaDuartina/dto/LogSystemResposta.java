package br.com.escolaDuartina.dto;

import java.time.LocalDateTime;

public record LogSystemResposta(
		Long id,
		String acao,
		Long usuarioId,
		String nomeUsuario,
		LocalDateTime dataHora) {
}
