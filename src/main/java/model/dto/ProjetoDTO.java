package model.dto;

import java.time.format.DateTimeFormatter;

import model.Projeto;

public class ProjetoDTO {
	private Long   id;
	private String nome;
	private String dataCriacao;
	private int qtdeEmpregados;

	public ProjetoDTO(Projeto objOriginal)  {
		super();
		this.id = objOriginal.getId();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		this.dataCriacao = objOriginal.getDataCriacao().format(formatter); 
		this.nome = objOriginal.getNome();
		this.qtdeEmpregados = objOriginal.getListaEmpregados().size();
	}

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}
	
	public String getDataCriacao() {
		return dataCriacao;
	}
	
	public int getQtdeEmpregados() {
		return qtdeEmpregados;
	}
	
	
	@Override
	public String toString() {
		return nome;
	}
}
