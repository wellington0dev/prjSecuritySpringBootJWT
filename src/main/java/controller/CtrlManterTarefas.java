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
import model.Tarefa;
import model.dto.TarefaDTO;
import service.ServiceManterTarefas;

@RestController
@RequestMapping("/proj/{idProjeto}/tarefas")
public class CtrlManterTarefas {
	//
	// ATRIBUTOS
	//
	private final ServiceManterTarefas service;

	//
	// MÉTODOS
	//
	public CtrlManterTarefas(ServiceManterTarefas service) {
		System.out.println("\nCtrlManterTarefas Instanciado!");
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/incluir")
	public ResponseEntity<?> incluirTarefa(@PathVariable("idProjeto") long idProjeto,
			@RequestBody Tarefa nova) {
		try {
			Tarefa salva = service.incluirTarefa(idProjeto, nova);
			return ResponseEntity.status(HttpStatus.CREATED).body(new TarefaDTO(salva));
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
	@GetMapping("/listar")
	public ResponseEntity<?> listarTarefas(@PathVariable("idProjeto") long idProjeto) {
		try {
			List<TarefaDTO> lista = new ArrayList<>();
			for (Tarefa tarefa : service.listarTarefas(idProjeto)) {
				lista.add(new TarefaDTO(tarefa));
			}
			return ResponseEntity.ok(lista);
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
	@GetMapping("/listar/{numOrdem}")
	public ResponseEntity<?> listarTarefa(@PathVariable("idProjeto") long idProjeto,
			@PathVariable("numOrdem") int numOrdem) {
		try {
			Tarefa tarefa = service.obterTarefa(idProjeto, numOrdem);
			return ResponseEntity.ok(new TarefaDTO(tarefa));
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/alterar/{numOrdem}")
	public ResponseEntity<?> alterarTarefa(@PathVariable("idProjeto") long idProjeto,
			@PathVariable("numOrdem") int numOrdem,
			@RequestBody Tarefa tarefaAlterada) {
		try {
			Tarefa tarefa = service.alterarTarefa(idProjeto, numOrdem, tarefaAlterada);
			return ResponseEntity.ok(new TarefaDTO(tarefa));
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/remover/{numOrdem}")
	public ResponseEntity<?> removerTarefa(@PathVariable("idProjeto") long idProjeto,
			@PathVariable("numOrdem") int numOrdem) {
		try {
			service.removerTarefa(idProjeto, numOrdem);
			return ResponseEntity.ok("Tarefa removida");
		} catch (ModelException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
