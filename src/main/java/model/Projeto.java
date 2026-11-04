package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Projeto {
	//
	// ATRIBUTOS
	//	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String nome;
	
	@Column(nullable = true)
	@DateTimeFormat(pattern = "dd/mm/yyyy hh:mm")
	private LocalDateTime dataCriacao;
	
	//
	// ATRIBUTOS DE RELACIONAMENTO
	//
	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "projeto-empregado") 
	private List<Empregado> listaEmpregados = new ArrayList<>();

	@OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "projeto-tarefa")
	private List<Tarefa> listaTarefas = new ArrayList<>();

	//
	// MÉTODOS
	//
	public Projeto() {
		super();
	}

	public Projeto(String n) throws ModelException {
		super();
		this.setNome(n);
		this.dataCriacao = LocalDateTime.now(); 
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) throws ModelException {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String n) throws ModelException {
		Projeto.validarNome(n);
		this.nome = n;
	}
	
	public LocalDateTime getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(LocalDateTime data) {
		if(this.dataCriacao == null)
			this.dataCriacao = data;
	}

	public List<Empregado> getListaEmpregados() {
		return new ArrayList<Empregado>(this.listaEmpregados);
	}
	
	public void setListaEmpregados(List<Empregado> novaLista) {
		this.listaEmpregados = novaLista;
	}
	
	public boolean adicionarEmpregado(Empregado novo) {
		if(!this.listaEmpregados.contains(novo))
			return this.listaEmpregados.add(novo);
		return false;
	}
	
	public boolean removerEmpregado(Empregado ex) {
		if(this.listaEmpregados.contains(ex))
			return this.listaEmpregados.remove(ex);
		return false;
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

	
	@Override
	public String toString() {
		return nome;
	}

	public int compareTo(Projeto outro) {
		return this.nome.compareTo(outro.nome);
	}

	public static void validarNome(String nome) throws ModelException {
		if (nome == null || nome.length() > 50)
			throw new ModelException("O nome passado é inválido: " + nome);
		String regex = "[A-Za-zÀ-ÿ0-9 -]{2,50}";
		if(!nome.matches(regex))
			throw new ModelException("O nome passado está em um formato inválido: " + nome);
	}
}
