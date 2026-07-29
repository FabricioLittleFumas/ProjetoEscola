package br.com.noticiarioOficial.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.noticiarioOficial.model.Aluno;

@Repository
public interface AlunoRepository extends CrudRepository<Aluno, Long>{
	@Query("SELECT a FROM Aluno a WHERE a.nome LIKE %:nome%")
	ArrayList<Aluno> findByNome(String nome);

}
