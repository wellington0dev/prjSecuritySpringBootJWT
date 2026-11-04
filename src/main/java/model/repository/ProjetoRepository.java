package model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Projeto;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    Projeto findById(long id);
}