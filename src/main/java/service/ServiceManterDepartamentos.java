package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.Departamento;
import model.ModelException;
import model.repository.DepartamentoRepository;

@Service
public class ServiceManterDepartamentos {
	//
	// ATRIBUTOS (Instanciados automaticamente pelo Spring Boot)
	//
	private final DepartamentoRepository repositorio;

	//
	// MÉTODOS
	//
	public ServiceManterDepartamentos(DepartamentoRepository repo) {
		System.out.println("\n ServiceManterDepartamentos Instanciado! Repositório: " + repo.getClass());
		this.repositorio = repo;
	}

	public Departamento incluirDepartamento(Departamento novo) {
		System.out.println("Depto:" + novo);
		return repositorio.save(novo);
	}

	public List<Departamento> listarDepartamentos() {
		List<Departamento> lista = repositorio.findAll();
		lista.sort((a, b) -> a.getNome().compareTo(b.getNome()));
		return lista;
	}

	public Departamento obterDepartamento(long id) {
		Departamento depto = repositorio.findById(id);
		return depto;
	}

	public Departamento obterDepartamentoPelaSigla(String sigla) {
		Departamento depto = repositorio.findBySigla(sigla);
		return depto;
	}

	public Departamento alterarDepartamento(long id, Departamento deptoAlterado) throws ModelException {
		Departamento depto = repositorio.findById(id);
		if (depto == null)
			throw new ModelException("Departamento não encontrado.");
		depto.setSigla(deptoAlterado.getSigla());
		depto.setNome(deptoAlterado.getNome());
		repositorio.save(depto);
		return depto;
	}

	public void removerDepartamento(int id) throws ModelException {
		Departamento depto = repositorio.findById(id);
		if (depto == null)
			throw new ModelException("Departamento não encontrado.");
		repositorio.delete(depto);
	}
}