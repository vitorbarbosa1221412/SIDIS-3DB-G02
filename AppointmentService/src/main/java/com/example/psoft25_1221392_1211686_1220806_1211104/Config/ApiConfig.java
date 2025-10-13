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
            request.getHeaders().add("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxLGFkbWluQGV4YW1wbGUuY29tIiwicm9sZXMiOiJBRE1JTiIsImlzcyI6ImV4YW1wbGUuaW8iLCJleHAiOjE3NjA0MjUzNzgsImlhdCI6MTc2MDM4OTM3OCwidXNlcklkIjoxfQ.GnP1zOi8dmgtxANE1Ji4ENFHAtVfOWi-W1aAqfuNCcCq8P1CqRsM1IBY5IereIYX0wKalP--qeHQAIp2tlMzMjNvbfZKonp6EY82Omzb1oMMywBArNyW4_rZH5fjApA7lSylhaztPhEfq2nhgQOS7nN4Cw-dgQNktFUvBMwl80Gc4c-IpY7k7s1WyTLRR_0dRAYkQOum-Q9g1OPve2t5dM2MbThRJ0zkyY3-ON5mFa2elepWRcC4xo2N9i55A33Or8Pze7pjMXLpQWcZrbdQvn2KxCx0apGclzAg2o7i56num3N2J7LadjE6Gc8yKGU3pKFV_rRU7hAmnXRfO-V0295j3L37u6ZHls5A2BIL4rYbQ-uR1Pf_yS0dImXb-hvxnh0eNWsBTFYVKUN5ddyEvHTmCbQB7j3K3w_Bu-ubAsYrkhkqDc1C_bk8GlqDw6Q8kGOHamwTzHiaqAI08r-gALt5nu0uEyVnbVWlNv2y_mKPtTmn068FmRQCIfvHTAvs-bA53SULeWwhmuXaGGIFFsc0nHJvtKxHgVJdgt9ZBqde-dmYerJP_5p371LWmNqeCqbCB5J5QxGwzRrNaskmpZI8eRv4h_NcdklM66H2tZ5MIOJJRKxFzQiA6THkxjxS7fLGSj0_3ZQ4QWgstQIkuPm5lDNQNowHzA4UaEh9Uu0");
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
