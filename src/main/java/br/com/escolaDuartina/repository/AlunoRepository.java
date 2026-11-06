package br.com.escolaDuartina.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.escolaDuartina.model.Aluno;

@Repository
public interface AlunoRepository extends CrudRepository<Aluno, Long>{
	List<Aluno> findByNomeContainingIgnoreCase(String nome);

}
