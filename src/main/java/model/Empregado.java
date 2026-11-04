package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "empregado")
public class Empregado {
	//
	// ATRIBUTOS
	//
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 14)
	private String cpf;

	@Column(nullable = false, length = 60)
	private String nome;

	@Column
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	private LocalDateTime dataHoraContratacao;

	//
	// ATRIBUTOS DE RELACIONAMENTO
	//
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_departamento", nullable = false)
	@JsonBackReference(value = "departamento-empregado")
	private Departamento departamento;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", nullable = true) // Alterado para true
	@JsonBackReference(value = "projeto-empregado")
	private Projeto projeto;

	@OneToMany(mappedBy = "empregado", fetch = FetchType.LAZY)
	@JsonManagedReference(value = "empregado-tarefa")
	private List<Tarefa> listaTarefas = new ArrayList<>();

	//
	// MÉTODOS
	//
	public Empregado() {
		super();
	}

	public Empregado(String cpf, String nome, Departamento departamento) throws ModelException {
		super();
		this.setCpf(cpf);
		this.setNome(nome);
		this.setDepartamento(departamento);
	}

	@PrePersist
	public void prePersist() {
		if (this.dataHoraContratacao == null) {
			this.dataHoraContratacao = LocalDateTime.now();
		}
	}

	public Long getId() {
		return id;
	}

	public void setIdEmpregado(Long id) {
		this.id = id;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) throws ModelException {
		Empregado.validarCpf(cpf);
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) throws ModelException {
		Empregado.validarNome(nome);
		this.nome = nome;
	}

	public LocalDateTime getDataHoraContratacao() {
		return this.dataHoraContratacao;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento novoDepto) throws ModelException {
		Empregado.validarDepartamento(novoDepto);
		if(this.departamento != null)
			this.departamento.removerEmpregado(this);
		this.departamento = novoDepto;
		novoDepto.adicionarEmpregado(this);
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto novoProjeto) {
		if(this.projeto != null)
			this.projeto.removerEmpregado(this);
		this.projeto = novoProjeto;
		if(novoProjeto != null)
			novoProjeto.adicionarEmpregado(this);
	}

	public List<Tarefa> getListaTarefas() {
		return new ArrayList<Tarefa>(this.listaTarefas);
	}

	public void setListaTarefas(List<Tarefa> novaLista) {
		this.listaTarefas = novaLista;
	}

	public boolean adicionarTarefa(Tarefa nova) {
		if(!this.listaTarefas.contains(nova))
			return this.listaTarefas.add(nova);
		return false;
	}

	public boolean removerTarefa(Tarefa ex) {
		if(this.listaTarefas.contains(ex))
			return this.listaTarefas.remove(ex);
		return false;
	}

	public static void validarCpf(String cpf) throws ModelException {
		if (cpf == null || cpf.length() == 0)
			throw new ModelException("O CPF não pode ser nulo!");
	}

	public static void validarNome(String nome) throws ModelException {
		if (nome == null || nome.length() == 0)
			throw new ModelException("O nome não pode ser nulo!");
	}

	public static void validarDepartamento(Departamento departamento) throws ModelException {
		if (departamento == null)
			throw new ModelException("É necessário associar o empregado a um departamento!");
	}
}