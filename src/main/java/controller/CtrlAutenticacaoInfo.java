package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class CtrlAutenticacaoInfo {

	public CtrlAutenticacaoInfo() {
    	System.out.println("\nCtrlAutenticacaoInfo Instanciado!");
	}
	
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        result.put("username", authentication.getName());

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .toList();

        result.put("roles", roles);

        return result;
    }
}