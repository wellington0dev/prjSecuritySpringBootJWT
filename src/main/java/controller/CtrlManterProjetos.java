package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.ModelException;
import model.Projeto;
import model.dto.ProjetoDTO;
import service.ServiceManterProjetos;

@RestController
@RequestMapping("/proj")
public class CtrlManterProjetos {
	//
	// ATRIBUTOS
	//
	private final ServiceManterProjetos service;

	//
	// MÉTODOS
	//
	public CtrlManterProjetos(ServiceManterProjetos service) {
		System.out.println("\nCtrlManterProjetos Instanciado!");
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/incluir")
	public ResponseEntity<?> incluirProjeto(@RequestBody Projeto novo) {
		Projeto salvo;
		try {
			salvo = service.incluirProjeto(novo);
		} catch (Exception me) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(me.getMessage());
		}
		return ResponseEntity.ok(salvo);
	}

	@PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
	@GetMapping("/listar")
	public ResponseEntity<?> listarProjetos() {
		List<ProjetoDTO> lista = new ArrayList<>();
		for (Projeto p : service.listarProjetos())
			lista.add(new ProjetoDTO(p));
		return ResponseEntity.ok(lista);
	}

	@PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
	@GetMapping("/listar/{paramId}")
	public ResponseEntity<?> listarProjeto(@PathVariable("paramId") int id) {
		Projeto projeto = service.obterProjeto(id);
		if (projeto == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projeto não encontrado");
		}
		return ResponseEntity.ok(projeto);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/alterar/{paramId}")
	public ResponseEntity<?> alterarProjeto(@PathVariable("paramId") long id,
			@RequestBody Projeto projAlterado) {
		Projeto proj;
		try {
			proj = service.alterarProjeto(id, projAlterado);
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok(proj);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/remover/{paramId}")
	public ResponseEntity<?> removerProjeto(@PathVariable("paramId") int id) {
		try {
			service.removerProjeto(id);
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok("Projeto removido");
	}
}