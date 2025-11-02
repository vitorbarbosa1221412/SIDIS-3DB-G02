package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import com.example.psoft25_1221392_1211686_1220806_1211104.services.RemoteUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

// IMPORTS PARA APACHE HTTP CLIENT 5.X
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "user.auth.search")
@Getter
@Setter
public class RestTemplateConfig {

    private List<String> urls;

    @Value("${auth.service.url}")
    private String authServiceBaseUrl;

    // PROPRIEDADES DE mTLS (CLIENTE)
    // CORREÇÃO: Adicionamos o prefixo '.ssl' aos placeholders
    @Value("${client.ssl.key-store}") // CORRIGIDO
    private Resource keyStore;
    @Value("${client.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${client.ssl.key-password}")
    private String keyPassword;
    @Value("${client.ssl.trust-store}") // CORRIGIDO
    private Resource trustStore;
    @Value("${client.ssl.trust-store-password}")
    private String trustStorePassword;


    /**
     * 1. Cria o RestTemplate customizado com mTLS ativado (usando Apache 5.x).
     *
     * @return RestTemplate configurado para mTLS sobre HTTPS.
     */
    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, CertificateException, IOException {

        // 1. Carregar o Key Store (Certificado do Cliente para autenticação mTLS)
        final KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var is = keyStore.getInputStream()) {
            clientKeyStore.load(is, keyStorePassword.toCharArray());
        }

        // 2. Carregar o Trust Store (Certificado da CA Raiz para confiar no servidor)
        final KeyStore clientTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (var is = trustStore.getInputStream()) {
            clientTrustStore.load(is, trustStorePassword.toCharArray());
        }

        // 3. Constrói o contexto SSL
        final SSLContext sslContext = SSLContexts.custom()
                // A. Key Material: O cliente apresenta seu certificado e chave privada.
                .loadKeyMaterial(clientKeyStore, keyPassword.toCharArray())
                // B. Trust Material: O cliente confia nos certificados do servidor (CA Raiz).
                .loadTrustMaterial(clientTrustStore, null)
                .build();

        // 4. Cria a Fábrica de Sockets SSL
        final var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                // Usar NoopHostnameVerifier é comum em ambientes internos de localhost/microsserviços para evitar falhas de validação de CN
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        // 5. Cria o Manager de Conexões com a Fábrica SSL
        final var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // 6. Cria o cliente HTTP do Apache com o contexto SSL configurado
        final CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // 7. Usa o cliente HTTP customizado no RestTemplate
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        // 8. Retorna o RestTemplate seguro
        return new RestTemplate(requestFactory);
    }

    /**
     * 2. Cria o RemoteUserDetailsService e passa os valores lidos via construtor
     */
    @Bean
    public RemoteUserDetailsService remoteUserDetailsService(RestTemplate restTemplate) {
        return new RemoteUserDetailsService(restTemplate, this.urls, this.authServiceBaseUrl);
    }
}