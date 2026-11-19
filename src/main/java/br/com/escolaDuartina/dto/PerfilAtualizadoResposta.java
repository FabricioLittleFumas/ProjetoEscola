package br.com.escolaDuartina.dto;

public record PerfilAtualizadoResposta(
		Integer id,
		String name,
		String email,
		boolean approved,
		String accessToken,
		String refreshToken) {
}
