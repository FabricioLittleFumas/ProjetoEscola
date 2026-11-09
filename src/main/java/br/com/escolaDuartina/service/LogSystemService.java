package br.com.escolaDuartina.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.escolaDuartina.model.LogSystem;
import br.com.escolaDuartina.dto.LogSystemResposta;
import br.com.escolaDuartina.repository.LogSystemRepository;
import br.com.escolaDuartina.repository.UserRepository;

@Service
public class LogSystemService {
	private final LogSystemRepository logRepository;
	private final UserRepository userRepository;

	public LogSystemService(LogSystemRepository logRepository, UserRepository userRepository) {
		this.logRepository = logRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public void registrar(String emailUsuario, String acao) {
		userRepository.findUserByEmail(emailUsuario)
				.ifPresent(usuario -> logRepository.save(new LogSystem(acao, usuario)));
	}

	@Transactional(readOnly = true)
	public Page<LogSystemResposta> listar(Pageable pageable) {
		return logRepository.findAllByOrderByDataHoraDesc(pageable).map(log -> new LogSystemResposta(
				log.getId(), log.getAcao(), log.getUsuario().getId().longValue(), log.getUsuario().getName(),
				log.getDataHora()));
	}
}
