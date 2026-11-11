package br.com.escolaDuartina.excecao;

import java.util.HashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.escolaDuartina.model.ErroTipo;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErroGenerico {
    @ExceptionHandler(PdfLeituraException.class)
    public ResponseEntity<ErroTipo> erroPdf(PdfLeituraException ex, HttpServletRequest request) {
        return respostaPdf(ex.getStatus(), ex.getMessage(), request);
    }

    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroTipo> arquivoGrande(HttpServletRequest request) {
        return respostaPdf(HttpStatus.PAYLOAD_TOO_LARGE, "O PDF deve ter at? 25 MB.", request);
    }

    private ResponseEntity<ErroTipo> respostaPdf(HttpStatus status, String message, HttpServletRequest request) {
        ErroTipo erro = new ErroTipo();
        erro.setMessage(message);
        erro.setError("Erro na leitura do PDF.");
        erro.setStatus(status.value());
        erro.setPath(request.getRequestURI());
        return new ResponseEntity<>(erro, status);
    }


	 @ExceptionHandler(Excecao.class)
	    public ResponseEntity<ErroTipo> erroNaoEncontrado(Excecao ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage(ex.getMessage());
	        erroTipo.setError("Recurso não encontrado.");
	        erroTipo.setStatus(HttpStatus.NOT_FOUND.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(ExcecaoInserirAluno.class)
	    public ResponseEntity<ErroTipo> erroInserir(ExcecaoInserirAluno ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage(ex.getMessage());
	        erroTipo.setError("Erro ao persistir aluno.");
	        erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ErroTipo> validError(MethodArgumentNotValidException ex, HttpServletRequest request) {
	        HashMap<String, String> erroValidacao = new HashMap<>();
	        ex.getAllErrors().forEach(erro -> erroValidacao.put(erro.getCode(), erro.getDefaultMessage()));

	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage("Campo de formulário em formato inválido.");
	        erroTipo.setErroValidacao(erroValidacao);
	        erroTipo.setError("Erro de validação.");
	        erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(DataIntegrityViolationException.class)
	    public ResponseEntity<ErroTipo> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage("Usuário já existe no banco de dados");
	        erroTipo.setError("Erro ao persistir.");
	        erroTipo.setStatus(HttpStatus.BAD_REQUEST.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(BadCredentialsException.class)
	    public ResponseEntity<ErroTipo> badCredentials(BadCredentialsException ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage("Email ou senha inválidos");
	        erroTipo.setError("Erro de autenticação");
	        erroTipo.setStatus(HttpStatus.UNAUTHORIZED.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.UNAUTHORIZED);
	    }

	    @ExceptionHandler(DisabledException.class)
	    public ResponseEntity<ErroTipo> usuarioAguardandoAprovacao(DisabledException ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage("Seu cadastro está aguardando a aprovação de um administrador.");
	        erroTipo.setError("Cadastro pendente de aprovação");
	        erroTipo.setStatus(HttpStatus.FORBIDDEN.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<>(erroTipo, HttpStatus.FORBIDDEN);
	    }

	    @ExceptionHandler(AuthenticationException.class)
	    public ResponseEntity<ErroTipo> authenticationError(AuthenticationException ex, HttpServletRequest request) {
	        ErroTipo erroTipo = new ErroTipo();
	        erroTipo.setMessage("Falha na autenticação");
	        erroTipo.setError("Erro de autenticação");
	        erroTipo.setStatus(HttpStatus.UNAUTHORIZED.value());
	        erroTipo.setPath(request.getRequestURI());
	        return new ResponseEntity<ErroTipo>(erroTipo, HttpStatus.UNAUTHORIZED);
	    }
	}
