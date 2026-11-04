package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Empregado;
import model.repository.EmpregadoRepository;

@Service
public class ServiceManterEmpregados {

    private final EmpregadoRepository repositorio;

    public ServiceManterEmpregados(EmpregadoRepository repositorio) {
		System.out.println("\n ServiceManterEmpregados Instanciado! Repositório: " + repositorio.getClass());    	
        this.repositorio = repositorio;
    }

    public List<Empregado> listarTodos() {
        return repositorio.findAll();
    }

    public Empregado buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public Empregado incluir(Empregado empregado) {
        empregado.setIdEmpregado(null); // força INSERT
        return repositorio.save(empregado);
    }

    public Empregado alterar(Long id, Empregado empregado) {
        empregado.setIdEmpregado(id);
        return repositorio.save(empregado);
    }

    public void remover(Long id) {
        repositorio.deleteById(id);
    }
}