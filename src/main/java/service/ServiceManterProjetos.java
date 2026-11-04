package service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import model.ModelException;
import model.Projeto;
import model.repository.ProjetoRepository;

@Service
public class ServiceManterProjetos {
	//
	// ATRIBUTOS (Instanciados automaticamente pelo Spring Boot)
	//
	private final ProjetoRepository repositorio;

	//
	// MÉTODOS
	//
	public ServiceManterProjetos(ProjetoRepository repo) {
		System.out.println("\n ServiceManterProjetos Instanciado! Repositório: " + repo.getClass());
		this.repositorio = repo;
	}

	public Projeto incluirProjeto(Projeto novo) {
		System.out.println("Projeto:" + novo);
		novo.setDataCriacao(LocalDateTime.now());
		return repositorio.save(novo);
	}

	public List<Projeto> listarProjetos() {
		List<Projeto> lista = repositorio.findAll();
		lista.sort((a, b) -> a.getNome().compareTo(b.getNome()));
		return lista;
	}

	public Projeto obterProjeto(long id) {
		Projeto projeto = repositorio.findById(id);
		return projeto;
	}

	public Projeto alterarProjeto(long id, Projeto projetoAlterado) throws ModelException {
		Projeto projeto = repositorio.findById(id);
		if (projeto == null)
			throw new ModelException("Projeto não encontrado.");
		projeto.setNome(projetoAlterado.getNome());
		repositorio.save(projeto);
		return projeto;
	}

	public void removerProjeto(int id) throws ModelException {
		Projeto projeto = repositorio.findById(id);
		if (projeto == null)
			throw new ModelException("Projeto não encontrado.");
		repositorio.delete(projeto);
	}
}