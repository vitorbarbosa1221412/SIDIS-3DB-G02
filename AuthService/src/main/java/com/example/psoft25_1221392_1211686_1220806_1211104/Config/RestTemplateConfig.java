package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import com.example.psoft25_1221392_1211686_1220806_1211104.services.RemoteUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
// FORÇA O SPRING A LER AS PROPRIEDADES QUE COMEÇAM POR "user.auth.search"
@ConfigurationProperties(prefix = "user.auth.search")
@Getter // Para que a Spring possa escrever na variável 'urls'
@Setter
public class RestTemplateConfig {

    // ESTA VARIÁVEL SERÁ PREENCHIDA COM user.auth.search.urls
    // (O setter é usado pelo Spring para injetar a lista antes do uso)
    private List<String> urls;

    // Injetamos a URL do próprio AuthService (ainda via @Value, pois é mais simples)
    @Value("${auth.service.url}")
    private String authServiceBaseUrl;

    // 1. Cria o RestTemplate (Bean simples)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // 2. Cria o RemoteUserDetailsService e passa os valores lidos via construtor
    @Bean
    public RemoteUserDetailsService remoteUserDetailsService(RestTemplate restTemplate) {
        // Passamos a lista de URLs (this.getUrls()) e a URL do AuthService
        return new RemoteUserDetailsService(restTemplate, this.getUrls(), this.authServiceBaseUrl);
    }
}
