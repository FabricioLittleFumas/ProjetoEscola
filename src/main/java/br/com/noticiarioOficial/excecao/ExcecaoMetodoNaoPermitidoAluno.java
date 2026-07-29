package br.com.noticiarioOficial.excecao;

public class ExcecaoMetodoNaoPermitidoAluno extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExcecaoMetodoNaoPermitidoAluno(String message) {
		super(message);
	}

	public ExcecaoMetodoNaoPermitidoAluno() {
		super();
	}
}
