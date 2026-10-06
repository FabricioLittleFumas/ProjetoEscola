package br.com.escolaDuartina.excecao;

public class ExcecaoInserirAluno extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExcecaoInserirAluno(String message) {
		super(message);
	}

	public ExcecaoInserirAluno() {
		super();
	}

}
