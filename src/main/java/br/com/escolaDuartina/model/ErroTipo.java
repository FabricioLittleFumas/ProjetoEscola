package br.com.escolaDuartina.model;

import java.util.HashMap;

public class ErroTipo {

	private int status;
	private String error;
	private String message;
	private String path;
	HashMap<String, String> erroValidacao;
	
	public ErroTipo() {
		erroValidacao = new HashMap<>();
	}

	public HashMap<String, String> getErroValidacao() {
		return erroValidacao;
	}

	public void setErroValidacao(HashMap<String, String> erroValidacao) {
		this.erroValidacao = erroValidacao;
	}

	@Override
	public String toString() {
		return "ErroTipo [status=" + status + ", error=" + error + ", message=" + message + ", path=" + path + "]";
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
