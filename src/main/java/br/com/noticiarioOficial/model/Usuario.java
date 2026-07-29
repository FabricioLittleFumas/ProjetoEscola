package br.com.noticiarioOficial.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, 
	generator = "seq_usuarios")
	@SequenceGenerator(name = "seq_usuarios", sequenceName = 
	"sq_usuarios", initialValue = 100, allocationSize = 100)
	private long id; 
	
	@Column(name = "nome")
	private String nome;
	
	private String email;
	
	private String senha;
}
