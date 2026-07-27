package br.com.noticiarioOficial.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.noticiarioOficial.model.Aluno;
import br.com.noticiarioOficial.repository.AlunoRepository;

@Service
public class AlunoService {

	@Autowired
	private AlunoRepository alunoRepository;

	public ArrayList<Aluno> buscarTodos() {
		ArrayList<Aluno> alunos = (ArrayList<Aluno>) alunoRepository.findAll();
		return alunos;
	}

	public Aluno buscarPorID(Long id) {
		Optional<Aluno> aluno = alunoRepository.findById(id);
		if (aluno.isPresent()) {
			return aluno.get();
		}
		return aluno.orElseThrow(() -> new RuntimeException("erro ao buscar noticia"));
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
		Aluno aluno2 = alunoRepository.save(aluno);
		return aluno2;
	}
}
