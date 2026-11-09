package br.com.escolaDuartina.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.escolaDuartina.model.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>{
	List<Aluno> findByNomeContainingIgnoreCase(String nome);
	Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}
