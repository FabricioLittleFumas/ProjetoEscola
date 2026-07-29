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
import jakarta.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, 
	generator = "seq_aluno")
	@SequenceGenerator(name = "seq_aluno", sequenceName = 
	"sq_aluno", initialValue = 100, allocationSize = 100)
	private Long id;
	private String nome;
	private Date dataSaida;

	@Enumerated(EnumType.STRING) // Armazena "ESTADUAL", "MUNICIPAL", etc.
	private Unidade unidade;

}
