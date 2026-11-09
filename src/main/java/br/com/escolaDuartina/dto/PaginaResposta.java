package br.com.escolaDuartina.dto;

import java.util.List;

public record PaginaResposta<T>(
		List<T> content,
		int page,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last) {
}
