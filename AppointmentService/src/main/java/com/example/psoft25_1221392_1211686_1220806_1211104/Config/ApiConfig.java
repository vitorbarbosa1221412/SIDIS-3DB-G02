package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.util.Collections;

@Configuration
public class ApiConfig {
    /*
     * Etags
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public RestTemplate restTemplateWithAuth() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(bearerTokenInterceptor()));
        return restTemplate;
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor() {
        return (request, body, execution) -> {
            // Normally, you'd dynamically fetch a token from your auth server here
            request.getHeaders().add("Authorization", "Bearer " + getAccessToken());
            return execution.execute(request, body);
        };
    }

    // For demo: Static token. Replace with real token fetching logic
    private String getAccessToken() {
        return "your-static-or-dynamic-access-token";
    }

    /*
     * OpenAPI
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Clínica").description("Projeto de Psoft").version("v1.0")
                        .contact(new Contact().name("José Neto").email("1211104@isep.ipp.pt")).termsOfService("TOC")
                        .license(new License().name("MIT").url("#")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
    }

}
