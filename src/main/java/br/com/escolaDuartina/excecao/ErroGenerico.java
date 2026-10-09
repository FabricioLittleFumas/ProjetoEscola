package br.com.escolaDuartina.excecao;

import java.util.HashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
		erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
		erroTipo.setPath(request.getRequestURI());
		return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroTipo> ValidError(MethodArgumentNotValidException ex, HttpServletRequest request) {
		HashMap<String, String> erroValidacao = new HashMap<>();
		erroTipo = new ErroTipo();
		erroTipo.setMessage("Campo de formulário em formato inválido.");
		ex.getAllErrors().forEach(erro ->{
			erroValidacao.put(erro.getCode(), erro.getDefaultMessage());
		});
		erroTipo.setErroValidacao(erroValidacao);
		erroTipo.setError("Erro ao persistir aluno.");
		erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
		erroTipo.setPath(request.getRequestURI());
		return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErroTipo> ExceptionAtualizar(DataIntegrityViolationException ex, HttpServletRequest request) {
		erroTipo = new ErroTipo();
		erroTipo.setMessage("Usuário já existe no banco de dados");
		erroTipo.setError("Erro ao persistir aluno.");
		erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
		erroTipo.setPath(request.getRequestURI());
		return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.BAD_REQUEST);
	}
	
	

}
