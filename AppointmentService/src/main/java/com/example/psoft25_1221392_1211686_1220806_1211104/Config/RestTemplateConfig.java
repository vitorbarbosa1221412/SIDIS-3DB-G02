package com.example.psoft25_1221392_1211686_1220806_1211104.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${external.services.connection-timeout:5000}")
    private int connectTimeoutMs;

    @Value("${external.services.read-timeout:10000}")
    private int readTimeoutMs;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        RestTemplate restTemplate = new RestTemplate(factory);
        ClientHttpRequestInterceptor authForwardingInterceptor = (request, body, execution) -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (attrs instanceof ServletRequestAttributes servletAttrs) {
                String auth = servletAttrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (auth != null && !auth.isEmpty()) {
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, auth);
                }
            }
            return execution.execute(request, body);
        };
        restTemplate.getInterceptors().add(authForwardingInterceptor);
        return restTemplate;
    }
}