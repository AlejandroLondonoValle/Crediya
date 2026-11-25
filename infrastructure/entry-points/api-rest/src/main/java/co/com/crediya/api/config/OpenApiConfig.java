package co.com.crediya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI crediYaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("CrediYa - Autenticación")
                .description("API para el registro y gestión de usuarios de CrediYa")
                .version("v1"));
    }
}