package br.com.escolaDuartina.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.escolaDuartina.model.Aluno;
import br.com.escolaDuartina.dto.PaginaResposta;
import br.com.escolaDuartina.service.AlunoService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:4200")
@RequestMapping("/alunos")
public class AlunoController {

	private final AlunoService alunoService;

	AlunoController(AlunoService alunoService) {
		this.alunoService = alunoService;
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping({"", "/"})
	public ResponseEntity<ArrayList<Aluno>> buscaTodos() {
		ArrayList<Aluno> alunos = alunoService.buscarTodos();
		return new ResponseEntity<ArrayList<Aluno>>(alunos, HttpStatus.OK);
	}
	
//	@PreAuthorize("hasAuthority('Admin')")
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/{id}")
	public ResponseEntity<Aluno> buscaPorID(@PathVariable(name = "id") Long id ){
		Aluno aluno = alunoService.buscarPorID(id);
		return new ResponseEntity<Aluno>(aluno, HttpStatus.OK);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/nome/{nome}")
	public ResponseEntity<ArrayList<Aluno>> buscaPorNome(@PathVariable(name = "nome") String nome){
		ArrayList<Aluno> alunos = alunoService.buscaPorNome(nome);
		return new ResponseEntity<ArrayList<Aluno>> (alunos, HttpStatus.OK);
	}

	@GetMapping("/buscar")
	public ResponseEntity<ArrayList<Aluno>> pesquisarPorNome(@RequestParam(name = "nome", defaultValue = "") String nome) {
		return ResponseEntity.ok(alunoService.buscaPorNome(nome));
	}

	@GetMapping("/paginado")
	public ResponseEntity<PaginaResposta<Aluno>> buscarPaginado(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sort", defaultValue = "nome") String sort,
			@RequestParam(name = "direction", defaultValue = "ASC") String direction,
			@RequestParam(name = "nome", defaultValue = "") String nome) {
		var resultado = alunoService.buscarPagina(page, size, sort, direction, nome);
		var resposta = new PaginaResposta<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
				resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast());
		return ResponseEntity.ok(resposta);
	}
	
	@CrossOrigin(origins = "http://localhost:4200")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarPorId(@PathVariable(name = "id") Long id, HttpServletRequest request){
		Aluno excluido = alunoService.deletarPorId(id);
		request.setAttribute("logSystem.oldObject", excluido == null ? Map.of("id", id) : excluido);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@PutMapping("/{id}")
	public ResponseEntity<Aluno> atualizaAluno(@PathVariable(name = "id") long id, @RequestBody Aluno AlunoAtualizado,
			HttpServletRequest request){
		var atualizacao = alunoService.atualizarAluno(id, AlunoAtualizado);
		request.setAttribute("logSystem.oldObject", atualizacao.antigo());
		request.setAttribute("logSystem.newObject", atualizacao.novo());
		return new ResponseEntity<Aluno>(atualizacao.novo(), HttpStatus.ACCEPTED);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping({"", "/"})
	public ResponseEntity<Aluno> inserirAluno(@Valid @RequestBody Aluno aluno, HttpServletRequest request){
		Aluno aluno2 = alunoService.inserirAluno(aluno);
		request.setAttribute("logSystem.newObject", aluno2);
		return new ResponseEntity<Aluno>(aluno2, HttpStatus.CREATED);
	}

}
