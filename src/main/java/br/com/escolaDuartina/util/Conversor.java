package br.com.escolaDuartina.util;

import org.springframework.stereotype.Component;

@Component
public class Conversor {
	public Conversor() {
	}
	public String converter(String erroValidacao) {
		 String texto = erroValidacao;
		 
		 int index = texto.lastIndexOf("constraint");
	        
	        if (index != -1) {
	            String resultado = texto.substring(index + "constraint".length())
	                    .replaceAll("[\\[\\]\\s]", "");
	            return resultado;
	        }

	        return null;
	}

}
