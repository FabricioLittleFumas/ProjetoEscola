package br.com.noticiarioOficial.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.noticiarioOficial.model.Aluno;
import br.com.noticiarioOficial.service.AlunoService;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

	@Autowired
	private AlunoService alunoService;

	@GetMapping("/")
	public ResponseEntity<ArrayList<Aluno>> buscaTodos() {
		ArrayList<Aluno> alunos = alunoService.buscarTodos();
		return new ResponseEntity<ArrayList<Aluno>>(alunos, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Aluno> buscaPorID(@PathVariable(name = "id") Long id ){
		Aluno aluno = alunoService.buscarPorID(id);
		return new ResponseEntity<Aluno>(aluno, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarPorId(@PathVariable(name = "id") Long id){
		alunoService.deletarPorId(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Aluno> atualizaAluno(@PathVariable(name = "id") long id, @RequestBody Aluno AlunoAtualizado){
		Aluno aluno = alunoService.atualizarNoticia(id, AlunoAtualizado);
		return new ResponseEntity<Aluno>(aluno, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/")
	public ResponseEntity<Aluno> inserirAluno(@RequestBody Aluno aluno){
		Aluno aluno2 = alunoService.inserirAluno(aluno);
		return new ResponseEntity<Aluno>(aluno2, HttpStatus.CREATED);
	}

}
