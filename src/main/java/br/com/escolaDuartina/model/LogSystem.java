package br.com.escolaDuartina.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_system")
public class LogSystem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "acao", nullable = false, columnDefinition = "TEXT", updatable = false)
	private String acao;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false, updatable = false)
	private User usuario;

	@Column(name = "data_hora", nullable = false, updatable = false)
	private LocalDateTime dataHora;

	protected LogSystem() {
	}

	public LogSystem(String acao, User usuario) {
		if (acao == null || acao.isBlank()) {
			throw new IllegalArgumentException("A ação do log é obrigatória");
		}
		if (usuario == null || usuario.getId() == null) {
			throw new IllegalArgumentException("O usuário responsável pelo log é obrigatório");
		}
		this.acao = acao;
		this.usuario = usuario;
	}

	@PrePersist
	private void preencherDataHora() {
		if (dataHora == null) {
			dataHora = LocalDateTime.now();
		}
	}

	@PreUpdate
	private void bloquearAtualizacao() {
		throw new UnsupportedOperationException("Logs do sistema são imutáveis");
	}

	@PreRemove
	private void bloquearExclusao() {
		throw new UnsupportedOperationException("Logs do sistema não podem ser excluídos");
	}

	public Long getId() {
		return id;
	}

	public String getAcao() {
		return acao;
	}

	public User getUsuario() {
		return usuario;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}
}
