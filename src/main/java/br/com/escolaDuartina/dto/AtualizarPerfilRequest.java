package br.com.escolaDuartina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarPerfilRequest(
		@NotBlank(message = "O nome é obrigatório")
		@Size(min = 3, max = 100, message = "O nome deve ter entre {min} e {max} caracteres")
		@Pattern(regexp = "^\\S.*\\S$", message = "O nome não pode começar ou terminar com espaços")
		String name,
		@NotBlank(message = "O e-mail é obrigatório")
		@Email(message = "E-mail inválido")
		@Pattern(regexp = "^\\S.*\\S$", message = "O e-mail não pode começar ou terminar com espaços")
		String email,
		@Size(min = 8, message = "A nova senha deve ter pelo menos 8 caracteres")
		String password) {
}
