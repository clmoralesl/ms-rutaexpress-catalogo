package cl.rutaexpress.catalogo.config;

import cl.rutaexpress.catalogo.security.AutenticacionInternaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcSecurityConfig implements WebMvcConfigurer {

    private final AutenticacionInternaInterceptor autenticacionInternaInterceptor;

    public WebMvcSecurityConfig(AutenticacionInternaInterceptor autenticacionInternaInterceptor) {
        this.autenticacionInternaInterceptor = autenticacionInternaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacionInternaInterceptor)
                .addPathPatterns("/api/catalogo/**")
                .excludePathPatterns(
                        "/api/publico/salud",
                        "/actuator",
                        "/actuator/**"
                );
    }
}
