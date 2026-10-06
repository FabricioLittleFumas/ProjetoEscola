package br.com.escolaDuartina.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.escolaDuartina.excecao.Excecao;
import br.com.escolaDuartina.excecao.ExcecaoInserirAluno;
import br.com.escolaDuartina.model.Aluno;
import br.com.escolaDuartina.repository.AlunoRepository;

@Service
public class AlunoService {

	private final AlunoRepository alunoRepository;

	AlunoService(AlunoRepository alunoRepository) {
		this.alunoRepository = alunoRepository;
	}

	public ArrayList<Aluno> buscarTodos() {
		ArrayList<Aluno> alunos = (ArrayList<Aluno>) alunoRepository.findAll();
		return alunos;
	}

	public Aluno buscarPorID(Long id) {
		Optional<Aluno> aluno = alunoRepository.findById(id);
		if (aluno.isPresent()) {
			return aluno.get();
		}
		return aluno.orElseThrow(() -> new Excecao("erro ao buscar aluno"));
	}

	public void deletarPorId(Long id) {
		alunoRepository.deleteById(id);
	}

	public Aluno atualizarNoticia(long id, Aluno alunoAtualizado) {
		// TODO Auto-generated method stub
		Aluno aluno = buscarPorID(id);
		BeanUtils.copyProperties(alunoAtualizado, aluno, "id");
		return alunoRepository.save(aluno);
	}

	public Aluno inserirAluno(Aluno aluno) {
		try {
			System.out.println("aq");
			System.out.println(aluno);
			Aluno aluno2 = alunoRepository.save(aluno);
			return aluno2;
		} catch (DataIntegrityViolationException e) {
			throw new ExcecaoInserirAluno("erro ao inserir aluno.");
		}
	}

	public ArrayList<Aluno> buscaPorNome(String nome) {
		// TODO Auto-generated method stub
		ArrayList<Aluno> alunos =  alunoRepository.findByNome(nome);
		return alunos;
	}
}
