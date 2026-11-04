package model;

import java.util.ArrayList;
import java.util.List;

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
public class Departamento {
	//
	// ATRIBUTOS
	//
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique=true, length = 2)
	private String sigla;

	@Column(nullable = false, length = 40)
	private String nome;
	
	//
	// ATRIBUTOS DE RELACIONAMENTO
	//
	
	@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference(value = "departamento-empregado") // Indica que NO JSON um objeto Departamento irá incluir os objetos Empregados. 
	private List<Empregado> listaEmpregados = new ArrayList<>();

	//
	// MÉTODOS
	//
	public Departamento() {
		super();
	}

	public Departamento(String s, String n) throws ModelException {
		super();
		this.setSigla(s);
		this.setNome(n);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) throws ModelException {
		this.id = id;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String s) throws ModelException {
		Departamento.validarSigla(s);
		this.sigla = s;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String n) throws ModelException {
		Departamento.validarNome(n);
		this.nome = n;
	}

	public List<Empregado> getEmpregados() {
		return new ArrayList<Empregado>(this.listaEmpregados);
	}
	
	public void setEmpregados(List<Empregado> novaLista) {
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

	
	@Override
	public String toString() {
		return nome;
	}

	public int compareTo(Departamento outro) {
		return this.nome.compareTo(outro.nome);
	}

	public static void validarSigla(String sigla) throws ModelException {
		if (sigla == null || sigla.length() != 2)
			throw new ModelException("A sigla passada é inválida: " + sigla);
	}

	public static void validarNome(String nome) throws ModelException {
		if (nome == null || nome.length() > 40)
			throw new ModelException("O nome passado é inválido: " + nome);
	}
}
