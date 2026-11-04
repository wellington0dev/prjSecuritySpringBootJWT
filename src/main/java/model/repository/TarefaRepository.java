package model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

	List<Tarefa> findByProjetoIdOrderByNumOrdemAsc(long idProjeto);

	Tarefa findByProjetoIdAndNumOrdem(long idProjeto, int numOrdem);

	boolean existsByProjetoIdAndNumOrdem(long idProjeto, int numOrdem);
}
