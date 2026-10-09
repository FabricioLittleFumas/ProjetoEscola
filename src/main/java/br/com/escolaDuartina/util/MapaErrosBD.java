package br.com.escolaDuartina.util;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapaErrosBD {
	@Autowired
	Conversor conversor;
	
	private HashMap<String, String> mapaErros;
	
	public MapaErrosBD() {
		mapaErros = new HashMap<>();
		mapaErros.put("data_saida", "Data em formato inválido");
		mapaErros.put("aluno_nome_key", "Nome já existe no banco de dados");
	}
	
	public String getErros(String erro) {
		return mapaErros.get(conversor.converter(erro));
	}
}
