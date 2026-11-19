package br.com.escolaDuartina.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.dto.AprovacaoUsuarioRequest;
import br.com.escolaDuartina.dto.AtualizarPerfilRequest;
import br.com.escolaDuartina.dto.PerfilAtualizadoResposta;
import br.com.escolaDuartina.dto.UsuarioResumo;
import br.com.escolaDuartina.repository.IUserService;
import br.com.escolaDuartina.service.UserServiceImpl;
import br.com.escolaDuartina.security.JwtTokenUtil;
import br.com.escolaDuartina.security.LogSystemInterceptor;
import br.com.escolaDuartina.security.UserInfoUserDetailsService;
import br.com.escolaDuartina.service.RefreshTokenService;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/usuario")
public class UserController {

	@Autowired
	private IUserService userService;
	@Autowired
	private UserServiceImpl userManagementService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserInfoUserDetailsService userDetailsService;
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	
	// Read Form data to save into DB
	@PostMapping("/saveUser")
	public ResponseEntity<Integer> saveUser(
			@RequestBody User user, HttpServletRequest request) {
		Integer id = userService.saveUser(user);
		request.setAttribute("logSystem.newObject", new UsuarioResumo(id, user.getName(), user.getEmail(), user.isApproved()));
		return new ResponseEntity<Integer>(id, HttpStatus.ACCEPTED);
	}

	@GetMapping
	public ResponseEntity<List<UsuarioResumo>> listarUsuarios() {
		List<UsuarioResumo> usuarios = userManagementService.listarUsuarios().stream()
				.map(user -> new UsuarioResumo(user.getId(), user.getName(), user.getEmail(), user.isApproved()))
				.toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/pendentes")
	public ResponseEntity<List<UsuarioResumo>> listarUsuariosPendentes() {
		List<UsuarioResumo> usuarios = userManagementService.listarUsuariosPendentes().stream()
				.map(user -> new UsuarioResumo(user.getId(), user.getName(), user.getEmail(), user.isApproved()))
				.toList();
		return ResponseEntity.ok(usuarios);
	}

	@GetMapping("/perfil")
	public ResponseEntity<UsuarioResumo> consultarPerfil(@AuthenticationPrincipal UserDetails principal) {
		User user = userManagementService.buscarPorEmail(principal.getUsername());
		return ResponseEntity.ok(new UsuarioResumo(user.getId(), user.getName(), user.getEmail(), user.isApproved()));
	}

	@PatchMapping("/perfil")
	public ResponseEntity<PerfilAtualizadoResposta> atualizarPerfil(
			@AuthenticationPrincipal UserDetails principal,
			@Valid @RequestBody AtualizarPerfilRequest request,
			HttpServletRequest httpRequest) {
		User antigo = userManagementService.buscarPorEmail(principal.getUsername());
		httpRequest.setAttribute("logSystem.oldObject",
				new UsuarioResumo(antigo.getId(), antigo.getName(), antigo.getEmail(), antigo.isApproved()));

		User atualizado = userManagementService.atualizarPerfil(
				principal.getUsername(), request.name(), request.email(), request.password());
		UsuarioResumo resposta = new UsuarioResumo(
				atualizado.getId(), atualizado.getName(), atualizado.getEmail(), atualizado.isApproved());
		httpRequest.setAttribute("logSystem.newObject", resposta);
		httpRequest.setAttribute(LogSystemInterceptor.LOG_EMAIL_ATTRIBUTE, atualizado.getEmail());
		UserDetails principalAtualizado = userDetailsService.loadUserByUsername(atualizado.getEmail());
		String accessToken = jwtTokenUtil.generateAccessToken(principalAtualizado);
		String refreshToken = refreshTokenService.createRefreshToken(atualizado.getEmail());
		return ResponseEntity.ok(new PerfilAtualizadoResposta(
				resposta.id(), resposta.name(), resposta.email(), resposta.approved(), accessToken, refreshToken));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<UsuarioResumo> atualizarUsuario(
			@PathVariable("id") Integer id,
			@Valid @RequestBody AtualizarPerfilRequest request,
			HttpServletRequest httpRequest) {
		User antigo = userManagementService.buscarPorId(id);
		httpRequest.setAttribute("logSystem.oldObject",
				new UsuarioResumo(antigo.getId(), antigo.getName(), antigo.getEmail(), antigo.isApproved()));
		User atualizado = userManagementService.atualizarUsuario(
				id, request.name(), request.email(), request.password());
		UsuarioResumo resposta = new UsuarioResumo(
				atualizado.getId(), atualizado.getName(), atualizado.getEmail(), atualizado.isApproved());
		httpRequest.setAttribute("logSystem.newObject", resposta);
		httpRequest.setAttribute(LogSystemInterceptor.LOG_EMAIL_ATTRIBUTE, atualizado.getEmail());
		return ResponseEntity.ok(resposta);
	}

	@PatchMapping("/{id}/aprovacao")
	public ResponseEntity<UsuarioResumo> atualizarAprovacao(
			@PathVariable("id") Integer id,
			@RequestBody AprovacaoUsuarioRequest request,
			HttpServletRequest httpRequest) {
		User antigo = userManagementService.buscarPorId(id);
		httpRequest.setAttribute("logSystem.oldObject",
				new UsuarioResumo(antigo.getId(), antigo.getName(), antigo.getEmail(), antigo.isApproved()));
		User user = userManagementService.atualizarAprovacao(id, request.aprovado());
		UsuarioResumo atualizado = new UsuarioResumo(user.getId(), user.getName(), user.getEmail(), user.isApproved());
		httpRequest.setAttribute("logSystem.newObject", atualizado);
		return ResponseEntity.ok(atualizado);
	}
}
