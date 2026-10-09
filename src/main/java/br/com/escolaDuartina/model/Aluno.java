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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	
	@NotBlank(message = "O nome é obrigatório")
	@Size(min = 3, max = 100, message = "O nome deve ter entre {min} e {max} caracteres")
	@NotEmpty
	@NotNull
	@Column(nullable = false, unique = true)
	private String nome;

	@Temporal(TemporalType.DATE)
	
	@Past(message = "A data deve ser no passado")
	@Column(name = "data_saida", nullable = false)
//	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date data_saida;

	@Enumerated(EnumType.STRING) // Armazena "ESTADUAL", "MUNICIPAL", etc.
	private Unidade unidade;

}
