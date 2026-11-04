package controller.filter;

import org.hibernate.mapping.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import model.Usuario;
import model.repository.UsuarioRepository;

@Configuration
public class UserConfig {
	//
	// ATRIBUTOS (Instanciados automaticamente pelo Spring Boot)
	//
	private final UsuarioRepository repositorio;

	//
	// MÉTODOS
	//
	public UserConfig(UsuarioRepository repo) {
		System.out.println("\nUserConfig Instanciado! Repositório de Usuários ---> " + repo.getClass() + "\n");
		this.repositorio = repo;
	}

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		System.out.println("userDetailsService");
        return username -> {
        	System.out.println("meu userDetailsService chamado: " + this.hashCode());
            Usuario usr = repositorio.findByConta(username);

            if (usr == null) {
                throw new UsernameNotFoundException("Usuário não encontrado");
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(usr.getConta())
                    .password(usr.getSenhaBCrypt()) // agora é BCrypt!
                    .roles(usr.getPapel())
                    .build();
        };
    }
}