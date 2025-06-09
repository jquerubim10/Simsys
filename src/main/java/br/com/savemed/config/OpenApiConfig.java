package br.com.savemed.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rest Api's RestFull Savemed")
                        .version("V1")
                        .description("Rest Api's RestFull Savemed")
                        .termsOfService("https://br.com.renan/restapi")
                        .license(new License().name("Apache 2.0").url("https://br.com.renan/restapi")));
    }
}
