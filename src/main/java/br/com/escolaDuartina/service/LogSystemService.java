package br.com.escolaDuartina.service;

import java.util.List;

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
	public void registrar(String emailUsuario, List<String> acoes) {
		userRepository.findUserByEmail(emailUsuario).ifPresent(usuario ->
				acoes.forEach(acao -> logRepository.save(new LogSystem(acao, usuario))));
	}

	@Transactional
	public void registrarLogin(String emailUsuario, int status) {
		userRepository.findUserByEmail(emailUsuario).ifPresent(usuario ->
				logRepository.save(new LogSystem("Login realizado | Resultado: " + resultado(status), usuario)));
	}

	@Transactional
	public void registrarConsultaAposLogin(String emailUsuario, int status) {
		userRepository.findUserByEmail(emailUsuario).ifPresent(usuario ->
				logRepository.save(new LogSystem("Consultou usuários pendentes após entrar | Resultado: "
						+ resultado(status), usuario)));
	}

	private String resultado(int status) {
		return status >= 200 && status < 300 ? "concluído" : "falhou (HTTP " + status + ")";
	}

	@Transactional(readOnly = true)
	public Page<LogSystemResposta> listar(Pageable pageable) {
		return listar("", pageable);
	}

	@Transactional(readOnly = true)
	public Page<LogSystemResposta> listar(String acao, Pageable pageable) {
		String filtro = acao == null ? "" : acao.trim();
		Page<LogSystem> pagina = filtro.isEmpty()
				? logRepository.findAllByOrderByDataHoraDescIdDesc(pageable)
				: logRepository.buscarPorAcaoSemAcento(filtro, pageable);
		return pagina.map(log -> new LogSystemResposta(
				log.getId(), log.getAcao(), log.getUsuario().getId().longValue(), log.getUsuario().getName(),
				log.getDataHora()));
	}
}
