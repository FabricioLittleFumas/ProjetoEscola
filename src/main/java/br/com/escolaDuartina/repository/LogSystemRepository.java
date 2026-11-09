package br.com.escolaDuartina.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import br.com.escolaDuartina.model.LogSystem;

public interface LogSystemRepository extends Repository<LogSystem, Long> {
	LogSystem save(LogSystem log);

	Page<LogSystem> findAllByOrderByDataHoraDesc(Pageable pageable);
}
