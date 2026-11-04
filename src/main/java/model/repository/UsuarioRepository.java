package model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import model.Departamento;
import model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByConta(String conta);
    Usuario findById(long id);
}