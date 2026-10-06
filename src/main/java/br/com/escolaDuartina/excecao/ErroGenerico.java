package br.com.escolaDuartina.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.escolaDuartina.model.ErroTipo;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErroGenerico {

	private ErroTipo erroTipo;

	@ExceptionHandler(Excecao.class)
	public ResponseEntity<ErroTipo> erroNaoEncontrado(Excecao ex, HttpServletRequest request) {
		erroTipo = new ErroTipo();
		erroTipo.setMessage(ex.getMessage());
		erroTipo.setError("Aluno não existe na base de dados.");
		erroTipo.setStatus(HttpStatus.NOT_FOUND.value());
		erroTipo.setPath(request.getRequestURI());
		return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ExcecaoInserirAluno.class)
	public ResponseEntity<ErroTipo> erroNaoEncontrado(ExcecaoInserirAluno ex, HttpServletRequest request) {
		erroTipo = new ErroTipo();
		erroTipo.setMessage(ex.getMessage());
		erroTipo.setError("Erro ao persistir aluno.");
		erroTipo.setStatus(HttpStatus.NOT_FOUND.value());
		erroTipo.setPath(request.getRequestURI());
		return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.NOT_FOUND);
	}

}
