package service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import model.Empregado;
import model.ModelException;
import model.Projeto;
import model.Tarefa;
import model.repository.EmpregadoRepository;
import model.repository.ProjetoRepository;
import model.repository.TarefaRepository;

@Service
public class ServiceManterTarefas {
	//
	// ATRIBUTOS (Instanciados automaticamente pelo Spring Boot)
	//
	private final TarefaRepository repositorio;
	private final ProjetoRepository projetoRepositorio;
	private final EmpregadoRepository empregadoRepositorio;

	//
	// MÉTODOS
	//
	public ServiceManterTarefas(TarefaRepository repo,
			ProjetoRepository projetoRepo,
			EmpregadoRepository empregadoRepo) {
		System.out.println("\n ServiceManterTarefas Instanciado! Repositório: " + repo.getClass());
		this.repositorio = repo;
		this.projetoRepositorio = projetoRepo;
		this.empregadoRepositorio = empregadoRepo;
	}

	@Transactional
	public Tarefa incluirTarefa(long idProjeto, Tarefa nova) throws ModelException {
		Projeto projeto = obterProjeto(idProjeto);
		if (nova == null) {
			throw new ModelException("Os dados da tarefa não foram informados.");
		}
		Tarefa.validarNumOrdem(nova.getNumOrdem());
		Tarefa.validarDescricao(nova.getDescricao());

		if (repositorio.existsByProjetoIdAndNumOrdem(idProjeto, nova.getNumOrdem())) {
			throw new ModelException("Já existe uma tarefa com o número de ordem "
					+ nova.getNumOrdem() + " neste projeto.");
		}

		Empregado empregado = obterEmpregadoDaTarefa(nova);

		nova.setId(null);
		nova.setProjeto(projeto);
		nova.setEmpregado(empregado);
		nova.setDataCriacao(LocalDateTime.now());

		return repositorio.save(nova);
	}

	public List<Tarefa> listarTarefas(long idProjeto) throws ModelException {
		obterProjeto(idProjeto);
		return repositorio.findByProjetoIdOrderByNumOrdemAsc(idProjeto);
	}

	public Tarefa obterTarefa(long idProjeto, int numOrdem) throws ModelException {
		obterProjeto(idProjeto);
		Tarefa tarefa = repositorio.findByProjetoIdAndNumOrdem(idProjeto, numOrdem);
		if (tarefa == null) {
			throw new ModelException("Tarefa de número de ordem " + numOrdem + " não encontrada neste projeto.");
		}
		return tarefa;
	}

	@Transactional
	public Tarefa alterarTarefa(long idProjeto, int numOrdem, Tarefa tarefaAlterada) throws ModelException {
		obterProjeto(idProjeto);
		Tarefa tarefa = obterTarefa(idProjeto, numOrdem);

		if (tarefaAlterada == null) {
			throw new ModelException("Os dados da tarefa não foram informados.");
		}
		Tarefa.validarNumOrdem(tarefaAlterada.getNumOrdem());
		Tarefa.validarDescricao(tarefaAlterada.getDescricao());

		if (tarefaAlterada.getNumOrdem() != numOrdem
				&& repositorio.existsByProjetoIdAndNumOrdem(idProjeto, tarefaAlterada.getNumOrdem())) {
			throw new ModelException("Já existe uma tarefa com o número de ordem "
					+ tarefaAlterada.getNumOrdem() + " neste projeto.");
		}

		Empregado empregado = obterEmpregadoDaTarefa(tarefaAlterada);

		tarefa.setNumOrdem(tarefaAlterada.getNumOrdem());
		tarefa.setDescricao(tarefaAlterada.getDescricao());
		tarefa.setEmpregado(empregado);

		return repositorio.save(tarefa);
	}

	@Transactional
	public void removerTarefa(long idProjeto, int numOrdem) throws ModelException {
		obterProjeto(idProjeto);
		Tarefa tarefa = obterTarefa(idProjeto, numOrdem);

		Projeto projeto = tarefa.getProjeto();
		if (projeto != null) {
			projeto.removerTarefa(tarefa);
		}

		Empregado empregado = tarefa.getEmpregado();
		if (empregado != null) {
			empregado.removerTarefa(tarefa);
		}

		repositorio.delete(tarefa);
	}

	private Projeto obterProjeto(long idProjeto) throws ModelException {
		Projeto projeto = projetoRepositorio.findById(idProjeto);
		if (projeto == null) {
			throw new ModelException("Projeto não encontrado.");
		}
		return projeto;
	}

	private Empregado obterEmpregadoDaTarefa(Tarefa tarefa) throws ModelException {
		if (tarefa.getEmpregado() == null || tarefa.getEmpregado().getId() == null) {
			throw new ModelException("É necessário associar a tarefa a um empregado.");
		}

		Empregado empregado = empregadoRepositorio.findById(tarefa.getEmpregado().getId().longValue());
		if (empregado == null) {
			throw new ModelException("Empregado não encontrado.");
		}
		return empregado;
	}
}
