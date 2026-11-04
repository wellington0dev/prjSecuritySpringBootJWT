package model.dto;

import java.time.LocalDateTime;

import model.Empregado;

public class EmpregadoDTO {
	private Long id;
	private String cpf;
	private String nome;
	private LocalDateTime dataHoraContratacao;
	private Long idDepartamento;
	private String nomeDepartamento;
	private Long idProjeto;
	private String nomeProjeto;
	
	public EmpregadoDTO(Empregado emp) {
		this.id = emp.getId();
		this.cpf = emp.getCpf();
		this.nome = emp.getNome();
		this.dataHoraContratacao = emp.getDataHoraContratacao();
		
		if (emp.getDepartamento() != null) {
			this.idDepartamento = emp.getDepartamento().getId();
			this.nomeDepartamento = emp.getDepartamento().getNome();
		}
		
		if (emp.getProjeto() != null) {
			this.idProjeto = emp.getProjeto().getId();
			this.nomeProjeto = emp.getProjeto().getNome();
		}
	}
	
	public Long getId() {
		return id;
	}

	public String getCpf() {
		return cpf;
	}

	public String getNome() {
		return nome;
	}

	public LocalDateTime getDataHoraContratacao() {
		return dataHoraContratacao;
	}
	
	public Long getIdDepartamento() {
		return idDepartamento;
	}
	
	public String getNomeDepartamento() {
		return nomeDepartamento;
	}

	public Long getIdProjeto() {
		return idProjeto;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}
}