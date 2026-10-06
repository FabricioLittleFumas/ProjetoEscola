package br.com.escolaDuartina.controller;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.escolaDuartina.model.Aluno;
import br.com.escolaDuartina.service.AlunoService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/alunos")
public class AlunoController {

	private final AlunoService alunoService;

	AlunoController(AlunoService alunoService) {
		this.alunoService = alunoService;
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/")
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
	
	@CrossOrigin(origins = "http://localhost:4200")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarPorId(@PathVariable(name = "id") Long id){
		alunoService.deletarPorId(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@PutMapping("/{id}")
	public ResponseEntity<Aluno> atualizaAluno(@PathVariable(name = "id") long id, @RequestBody Aluno AlunoAtualizado){
		Aluno aluno = alunoService.atualizarNoticia(id, AlunoAtualizado);
		return new ResponseEntity<Aluno>(aluno, HttpStatus.ACCEPTED);
	}
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/")
	public ResponseEntity<Aluno> inserirAluno(@RequestBody Aluno aluno){
		System.out.println("aluno no persist");
		System.out.println(aluno.getData_saida());
		Aluno aluno2 = alunoService.inserirAluno(aluno);
		return new ResponseEntity<Aluno>(aluno2, HttpStatus.CREATED);
	}

}
