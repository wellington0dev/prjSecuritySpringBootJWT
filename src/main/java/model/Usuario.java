package model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario implements Serializable {
	//
	// ATRIBUTOS
	//
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique=true, length = 20)
	private String conta;
	@Column(nullable = false, length = 80)
	private String senhaBCrypt;
	@Column(nullable = false, length = 15)
	private String papel;

	//
	// MÉTODOS
	//
	public Usuario() {		
		super();
	}
	
	public Usuario(String conta, String papel, String senhaBCrypt) {
		super();
		this.conta = conta;
		this.papel = papel;
		this.senhaBCrypt = senhaBCrypt;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getPapel() {
		return papel;
	}

	public void setPapel(String papel) {
		this.papel = papel;
	}

	public String getSenhaBCrypt() {
		return senhaBCrypt;
	}

	public void setSenhaBCrypt(String senhaBCrypt) {
		this.senhaBCrypt = senhaBCrypt;
	}
		
	@Override
	public String toString() {
		return "Usuario [conta=" + conta + ", senhaBCrypt=" + senhaBCrypt +  "]";
	}
}
