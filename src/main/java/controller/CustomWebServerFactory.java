package controller;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomWebServerFactory 
        implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

	public CustomWebServerFactory() {
    	System.out.println("\nCtrlManterEmpregados Instanciado!");
	}
	
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.setPort(8081);
    }
}
