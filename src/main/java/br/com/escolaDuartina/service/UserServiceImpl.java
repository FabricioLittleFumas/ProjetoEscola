package br.com.escolaDuartina.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.NoSuchElementException;

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.repository.UserRepository;
import br.com.escolaDuartina.repository.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public Integer saveUser(User user) {
		String passwd = user.getPassword();
		String encodedPasswod = passwordEncoder.encode(passwd);
		user.setPassword(encodedPasswod);
		user.setRoles(List.of("USER"));
		user.setApproved(false);
		user = userRepo.save(user);
		return user.getId();
	}

	@Transactional(readOnly = true)
	public List<User> listarUsuarios() {
		return userRepo.findAllByOrderByNameAsc();
	}

	@Transactional(readOnly = true)
	public List<User> listarUsuariosPendentes() {
		return userRepo.findByApprovedFalseOrderByNameAsc();
	}

	@Transactional(readOnly = true)
	public User buscarPorId(Integer id) {
		return userRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
	}

	@Transactional(readOnly = true)
	public User buscarPorEmail(String email) {
		return userRepo.findUserByEmail(email)
				.orElseThrow(() -> new NoSuchElementException("Usuário não encontrado"));
	}

	@Transactional
	public User atualizarPerfil(String emailAtual, String nome, String email, String novaSenha) {
		User usuario = buscarPorEmail(emailAtual);
		usuario.setName(nome.trim());
		usuario.setEmail(email.trim());
		if (novaSenha != null && !novaSenha.isBlank()) {
			usuario.setPassword(passwordEncoder.encode(novaSenha));
		}
		return userRepo.save(usuario);
	}

	@Transactional
	public User atualizarUsuario(Integer id, String nome, String email, String novaSenha) {
		User usuario = buscarPorId(id);
		String emailNormalizado = email.trim();
		if (userRepo.existsByEmailAndIdNot(emailNormalizado, id)) {
			throw new DataIntegrityViolationException("O e-mail informado já está cadastrado.");
		}
		usuario.setName(nome.trim());
		usuario.setEmail(emailNormalizado);
		if (novaSenha != null && !novaSenha.isBlank()) {
			usuario.setPassword(passwordEncoder.encode(novaSenha));
		}
		return userRepo.save(usuario);
	}

	@Transactional
	public User atualizarAprovacao(Integer id, boolean aprovado) {
		int linhasAtualizadas = userRepo.atualizarStatusAprovacao(id, aprovado);
		if (linhasAtualizadas == 0) {
			throw new IllegalArgumentException("Usuário não encontrado");
		}
		return userRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado após a atualização"));
	}
}
