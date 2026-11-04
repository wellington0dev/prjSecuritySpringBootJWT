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

import model.Departamento;
import model.ModelException;
import model.dto.DepartamentoDTO;
import service.ServiceManterDepartamentos;

@RestController
@RequestMapping("/depto")
public class CtrlManterDepartamentos {
	//
	// ATRIBUTOS
	//
    private final ServiceManterDepartamentos service;

    //
    // MÉTODOS
    //
    public CtrlManterDepartamentos(ServiceManterDepartamentos service) {
    	System.out.println("\nCtrlManterDepartamentos Instanciado!");
        this.service = service;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/incluir")
    public ResponseEntity<?> incluirDepartamento(@RequestBody Departamento novo) {
    	Departamento salvo;
    	try {
    		salvo = service.incluirDepartamento(novo);        
        } catch(Exception me) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(me.getMessage());
        }
        return ResponseEntity.ok(salvo);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
    @GetMapping("/listar")
    public ResponseEntity<?> listarDepartamentos() {    	    	
    	List<DepartamentoDTO> lista = new ArrayList<>();
    	for(Departamento d : service.listarDepartamentos())
    		lista.add(new DepartamentoDTO(d));    	
        return ResponseEntity.ok(service.listarDepartamentos());
    }

    @PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
    @GetMapping("/listar/{paramId}")
    public ResponseEntity<?> listarDepartamento(@PathVariable("paramId") int id) {
    	Departamento depto= service.obterDepartamento(id);
    	if (depto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Departamento não encontrado");
        }
        return ResponseEntity.ok(depto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LEITOR')")
    @GetMapping("/listarPelaSigla/{paramSigla}")
    public ResponseEntity<?> listarDepartamento(@PathVariable("paramSigla") String sigla) {
    	Departamento depto = service.obterDepartamentoPelaSigla(sigla);
    	if (depto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Departamento não encontrado");
        }
        return ResponseEntity.ok(depto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/alterar/{paramId}")
    public ResponseEntity<?> alterarDepartamento(@PathVariable("paramId") long id, @RequestBody Departamento deptoAlterado) {
    	Departamento depto;
		try {
			depto = service.alterarDepartamento(id, deptoAlterado);
		} catch (ModelException e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); 
		}
        return ResponseEntity.ok(depto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remover/{paramId}")
    public ResponseEntity<?> removerDepartamento(@PathVariable("paramId") int id) {
		try {
			service.removerDepartamento(id);
		} catch (ModelException e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); 
		}
        return ResponseEntity.ok("Departamento removido");
    }
}