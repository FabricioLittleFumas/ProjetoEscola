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

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.dto.AprovacaoUsuarioRequest;
import br.com.escolaDuartina.dto.UsuarioResumo;
import br.com.escolaDuartina.repository.IUserService;
import br.com.escolaDuartina.service.UserServiceImpl;
import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UserController {

	@Autowired
	private IUserService userService;
	@Autowired
	private UserServiceImpl userManagementService;
	
	
	// Read Form data to save into DB
	@PostMapping("/saveUser")
	public ResponseEntity<Integer> saveUser(
			@RequestBody User user) 
	{
		Integer id = userService.saveUser(user);
		String message = "User '"+id+"' saved successfully !";
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

	@PatchMapping("/{id}/aprovacao")
	public ResponseEntity<UsuarioResumo> atualizarAprovacao(
			@PathVariable("id") Integer id,
			@RequestBody AprovacaoUsuarioRequest request) {
		User user = userManagementService.atualizarAprovacao(id, request.aprovado());
		return ResponseEntity.ok(new UsuarioResumo(user.getId(), user.getName(), user.getEmail(), user.isApproved()));
	}
}
