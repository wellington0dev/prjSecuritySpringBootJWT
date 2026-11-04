package model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    Departamento findBySigla(String sigla);
    Departamento findById(long id);
}