package br.com.noticiarioOficial.model;

import java.io.Serializable;

public class JwtRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String email;
	private String password;

	// Construtores
	public JwtRequest() {
	}

	public JwtRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	// Getters e Setters
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}