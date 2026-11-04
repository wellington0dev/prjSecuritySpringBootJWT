package controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = { "controller", "service", "model" })
@EnableJpaRepositories(basePackages = "model.repository")
@EntityScan(basePackages = "model")
public class CtrlPrograma {

    public static void main(String[] args) {
        SpringApplication.run(CtrlPrograma.class, args);
    }

    @Bean
    public CommandLineRunner executarCriptografia() {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String senhaTextoPuro = "paracambi";
            String senhaCriptografada = encoder.encode(senhaTextoPuro);

            System.out.println("==================================================");
            System.out.println("Senha Original: " + senhaTextoPuro);
            System.out.println("Hash BCrypt Gerado: " + senhaCriptografada);
            System.out.println("==================================================");
        };
    }
}