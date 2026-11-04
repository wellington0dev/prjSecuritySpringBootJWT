package model;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tarefa", uniqueConstraints = @UniqueConstraint(
		name = "uk_tarefa_projeto_num_ordem",
		columnNames = { "id_projeto", "num_ordem" }))
public class Tarefa {
	//
	// ATRIBUTOS
	//
	@Id
	@Column(nullable = false, unique = true)
	private Long id;

	@Column(name = "num_ordem", nullable = false)
	private int numOrdem;

	@Column(nullable = false, length = 255)
	private String descricao;

	@Column(nullable = false)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	private LocalDateTime dataCriacao;

	//
	// ATRIBUTOS DE RELACIONAMENTO
	//
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", nullable = false)
	@JsonBackReference(value = "projeto-tarefa")
	private Projeto projeto;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empregado", nullable = false)
	@JsonBackReference(value = "empregado-tarefa")
	private Empregado empregado;

	//
	// MÉTODOS
	//
	public Tarefa() {
		super();
	}

	public Tarefa(int numOrdem, String descricao) throws ModelException {
		super();
		this.setNumOrdem(numOrdem);
		this.setDescricao(descricao);
		this.dataCriacao = LocalDateTime.now();
	}

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = ThreadLocalRandom.current()
					.nextLong(10_000_000L, 100_000_000L);
		}
		if (this.dataCriacao == null) {
			this.dataCriacao = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumOrdem() {
		return numOrdem;
	}

	public void setNumOrdem(int numOrdem) throws ModelException {
		Tarefa.validarNumOrdem(numOrdem);
		this.numOrdem = numOrdem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) throws ModelException {
		Tarefa.validarDescricao(descricao);
		this.descricao = descricao;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		if (this.dataCriacao == null) {
			this.dataCriacao = dataCriacao;
		}
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto novoProjeto) {
		if (this.projeto == novoProjeto) {
			return;
		}
		if (this.projeto != null) {
			this.projeto.removerTarefa(this);
		}
		this.projeto = novoProjeto;
		if (novoProjeto != null) {
			novoProjeto.adicionarTarefa(this);
		}
	}

	public Empregado getEmpregado() {
		return empregado;
	}

	public void setEmpregado(Empregado novoEmpregado) {
		if (this.empregado == novoEmpregado) {
			return;
		}
		if (this.empregado != null) {
			this.empregado.removerTarefa(this);
		}
		this.empregado = novoEmpregado;
		if (novoEmpregado != null) {
			novoEmpregado.adicionarTarefa(this);
		}
	}

	public static void validarNumOrdem(int numOrdem) throws ModelException {
		if (numOrdem <= 0) {
			throw new ModelException("O número de ordem deve ser maior que zero.");
		}
	}

	public static void validarDescricao(String descricao) throws ModelException {
		if (descricao == null || descricao.trim().isEmpty()) {
			throw new ModelException("A descrição da tarefa não pode ser nula ou vazia.");
		}
		if (descricao.length() > 255) {
			throw new ModelException("A descrição da tarefa deve ter no máximo 255 caracteres.");
		}
	}

	@Override
	public String toString() {
		return "Tarefa [numOrdem=" + numOrdem + ", descricao=" + descricao + "]";
	}

	public int compareTo(Tarefa outra) {
		return Integer.compare(this.numOrdem, outra.numOrdem);
	}
}
