package controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class CtrlAutenticacao {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public CtrlAutenticacao(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    public record LoginRequest(String conta, String senha) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
        		// o authManager irá utilizar o objeto UserDetailsService gerado na classe
        		// UserConfig para ver se a conta existe e se a senha corresponde à criptografia
        		// presente no repository
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.conta(), req.senha())
            );

            List<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtUtil.gerarToken(auth.getName(), roles);

            return ResponseEntity.ok(Map.of("meuJwt", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "Usuário ou senha inválidos"));
        }
    }
}