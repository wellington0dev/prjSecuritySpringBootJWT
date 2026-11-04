package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.Empregado;
import model.dto.EmpregadoDTO;
import service.ServiceManterEmpregados;

@RestController
@RequestMapping("/empregado")
public class CtrlManterEmpregados {

    private final ServiceManterEmpregados service;

    public CtrlManterEmpregados(ServiceManterEmpregados service) {
    	System.out.println("\nCtrlManterEmpregados Instanciado!");
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EmpregadoDTO>> listar() {
    	List<EmpregadoDTO> retorno = new ArrayList<>();
    	for(Empregado emp : service.listarTodos()) 
    		retorno.add(new EmpregadoDTO(emp));
        return ResponseEntity.ok(retorno);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<EmpregadoDTO> listarPorId(@PathVariable Long id) {
        Empregado emp = service.buscarPorId(id);

        if (emp == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new EmpregadoDTO(emp));
    }

    @PostMapping("/incluir")
    public ResponseEntity<EmpregadoDTO> incluir(@RequestBody Empregado empregado) {
        Empregado novo = service.incluir(empregado);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmpregadoDTO(novo));
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<EmpregadoDTO> alterar(@PathVariable Long id,
                                             @RequestBody Empregado empregado) {
        Empregado atualizado = service.alterar(id, empregado);
        return ResponseEntity.ok(new EmpregadoDTO(atualizado));
    }

    @DeleteMapping("/remover/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.ok().build();
    }
}