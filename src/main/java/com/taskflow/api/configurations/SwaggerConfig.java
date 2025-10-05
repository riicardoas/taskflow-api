package com.taskflow.api.configurations;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080"); 
        server.setDescription("Servidor local");

        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Tarefas")
                        .description("Documentação dos endpoints da API de tarefas internas")
                        .version("1.0"))
                .servers(List.of(server));
    }
    
}
