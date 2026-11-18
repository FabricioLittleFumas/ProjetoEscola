package br.com.escolaDuartina.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import br.com.escolaDuartina.model.LogSystem;

public interface LogSystemRepository extends Repository<LogSystem, Long> {
	LogSystem save(LogSystem log);

	Page<LogSystem> findAllByOrderByDataHoraDescIdDesc(Pageable pageable);

	@Query(value = "SELECT * FROM log_system l WHERE "
			+ "translate(lower(l.acao), 'áàâãäéèêëíìîïóòôöõúùûüç', 'aaaaaeeeeiiiiooooouuuuc') LIKE "
			+ "translate(lower(concat('%', :acao, '%')), 'áàâãäéèêëíìîïóòôöõúùûüç', 'aaaaaeeeeiiiiooooouuuuc') "
			+ "ORDER BY l.data_hora DESC, l.id DESC",
		countQuery = "SELECT count(*) FROM log_system l WHERE "
				+ "translate(lower(l.acao), 'áàâãäéèêëíìîïóòôöõúùûüç', 'aaaaaeeeeiiiiooooouuuuc') LIKE "
				+ "translate(lower(concat('%', :acao, '%')), 'áàâãäéèêëíìîïóòôöõúùûüç', 'aaaaaeeeeiiiiooooouuuuc')",
		 nativeQuery = true)
	Page<LogSystem> buscarPorAcaoSemAcento(@Param("acao") String acao, Pageable pageable);
}
