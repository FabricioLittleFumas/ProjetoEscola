package br.com.escolaDuartina.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.escolaDuartina.model.Aluno;
import jakarta.persistence.LockModeType;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long>{
	List<Aluno> findByNomeContainingIgnoreCase(String nome);
	Page<Aluno> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from Aluno a where a.id = :id")
	Optional<Aluno> buscarParaAlteracao(@Param("id") Long id);

}
