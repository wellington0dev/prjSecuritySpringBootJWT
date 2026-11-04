package model.dto;

import model.Departamento;

public class DepartamentoDTO {
	private Long id;
	private String sigla;
	private String nome;

	public DepartamentoDTO(Departamento objOriginal)  {
		super();
		this.id = objOriginal.getId();
		this.sigla = objOriginal.getSigla();
		this.nome = objOriginal.getNome();
	}

	public Long getId() {
		return id;
	}

	public String getSigla() {
		return sigla;
	}

	public String getNome() {
		return nome;
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
