package br.com.escolaDuartina.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.escolaDuartina.excecao.Excecao;
import br.com.escolaDuartina.excecao.ExcecaoInserirAluno;
import br.com.escolaDuartina.model.Aluno;
import br.com.escolaDuartina.repository.AlunoRepository;
import br.com.escolaDuartina.util.MapaErrosBD;

@Service
public class AlunoService {

	private final AlunoRepository alunoRepository;
	@Autowired
	MapaErrosBD mapaErrosBD;

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

	public Aluno atualizarAluno(long id, Aluno alunoAtualizado) {
		// TODO Auto-generated method stub
		Aluno aluno = buscarPorID(id);
		BeanUtils.copyProperties(alunoAtualizado, aluno, "id");
		return alunoRepository.save(aluno);
	}

	public Aluno inserirAluno(Aluno aluno) {
		try {
//			System.out.println("aq");
//			System.out.println(aluno);
			Aluno aluno2 = alunoRepository.save(aluno);
			return aluno2;
		} catch (DataIntegrityViolationException e) {
//			System.out.println("message");
//			System.out.println(e.getMessage());
//			System.out.println(mapaErrosBD.getErros(e.getMessage()));
//			String erroMessage = mapaErrosBD.getErros(e.getMessage());
			throw new ExcecaoInserirAluno(mapaErrosBD.getErros(e.getMessage()));
		}
	}

	public ArrayList<Aluno> buscaPorNome(String nome) {
		// TODO Auto-generated method stub
		ArrayList<Aluno> alunos = alunoRepository.findByNome(nome);
		return alunos;
	}
}
