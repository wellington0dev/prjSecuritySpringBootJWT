package model.dto;

import java.time.format.DateTimeFormatter;

import model.Tarefa;

public class TarefaDTO {
	private Long id;
	private int numOrdem;
	private String descricao;
	private String dataCriacao;
	private Long idProjeto;
	private String nomeProjeto;
	private Long idEmpregado;
	private String nomeEmpregado;

	public TarefaDTO(Tarefa objOriginal) {
		super();
		this.id = objOriginal.getId();
		this.numOrdem = objOriginal.getNumOrdem();
		this.descricao = objOriginal.getDescricao();

		if (objOriginal.getDataCriacao() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			this.dataCriacao = objOriginal.getDataCriacao().format(formatter);
		}

		if (objOriginal.getProjeto() != null) {
			this.idProjeto = objOriginal.getProjeto().getId();
			this.nomeProjeto = objOriginal.getProjeto().getNome();
		}

		if (objOriginal.getEmpregado() != null) {
			this.idEmpregado = objOriginal.getEmpregado().getId();
			this.nomeEmpregado = objOriginal.getEmpregado().getNome();
		}
	}

	public Long getId() {
		return id;
	}

	public int getNumOrdem() {
		return numOrdem;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public Long getIdProjeto() {
		return idProjeto;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public Long getIdEmpregado() {
		return idEmpregado;
	}

	public String getNomeEmpregado() {
		return nomeEmpregado;
	}
}
