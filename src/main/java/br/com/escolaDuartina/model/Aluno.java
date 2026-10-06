package br.com.escolaDuartina.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.escolaDuartina.enums.Unidade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

	@Temporal(TemporalType.DATE)
	@Column(name = "data_saida")
//	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date data_saida;

	@Enumerated(EnumType.STRING) // Armazena "ESTADUAL", "MUNICIPAL", etc.
	private Unidade unidade;

}
