package br.com.noticiarioOficial.model;

import java.io.Serializable;
import java.util.Date;

import br.com.noticiarioOficial.enums.Unidade;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Aluno implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
	private Date dataSaida;

	@Enumerated(EnumType.STRING) // Armazena "ESTADUAL", "MUNICIPAL", etc.
	private Unidade unidade;

}
