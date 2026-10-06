package br.com.escolaDuartina.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.escolaDuartina.model.User;
import br.com.escolaDuartina.repository.IUserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/usuario")
public class UserController {

	@Autowired
	private IUserService userService;
	
	
	// Read Form data to save into DB
	@PostMapping("/saveUser")
	public ResponseEntity<Integer> saveUser(
			@RequestBody User user) 
	{
		Integer id = userService.saveUser(user);
		String message = "User '"+id+"' saved successfully !";
		return new ResponseEntity<Integer>(id, HttpStatus.ACCEPTED);
	}
}