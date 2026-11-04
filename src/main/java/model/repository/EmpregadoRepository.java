package model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Empregado;

public interface EmpregadoRepository extends JpaRepository<Empregado, Long> {
    Empregado findById(long id);
    Empregado findByCpf(String cpf);
}