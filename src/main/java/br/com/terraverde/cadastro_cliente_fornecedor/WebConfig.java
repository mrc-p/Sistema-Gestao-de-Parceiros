package br.com.terraverde.cadastro_cliente_fornecedor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração MVC para mapear a URL raiz ("/")
 * para um arquivo estático padrão (index.html).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }
}