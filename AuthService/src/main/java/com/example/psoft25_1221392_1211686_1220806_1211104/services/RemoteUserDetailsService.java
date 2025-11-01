package com.example.psoft25_1221392_1211686_1220806_1211104.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço que implementa a busca federada de utilizadores (RemoteUserDetailsService).
 * Procura por credenciais sequencialmente em múltiplos microsserviços (Physician, Patient, etc.).
 *
 * NOTA: Os valores de configuração são injetados via construtor pelo RestTemplateConfig
 * para contornar o problema de injeção @Value.
 */
@Service
public class RemoteUserDetailsService implements UserDetailsService {

    private final RestTemplate restTemplate;
    private final List<String> userAuthSearchUrls; // Lista de URLs (Ex: Physician, Patient)
    private final String authServiceBaseUrl; // URL do próprio AuthService

    // Construtor manual que recebe todas as dependências do RestTemplateConfig
    public RemoteUserDetailsService(RestTemplate restTemplate, List<String> userAuthSearchUrls, String authServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.userAuthSearchUrls = userAuthSearchUrls;
        this.authServiceBaseUrl = authServiceBaseUrl;
    }


    /**
     * Obtém um Token de Serviço temporário do próprio AuthService.
     * Este token é usado para autorizar a chamada interna ao /api/internal/users.
     */
    private String getServiceToken() {
        // CRÍTICO: Usa a base URL do próprio serviço (https://localhost:8443)
        String tokenUrl = this.authServiceBaseUrl + "/api/auth/service-token";

        // Esta chamada DEVE funcionar porque o RestTemplateConfig garante o Trust Store
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                null, // Não enviamos credenciais, o endpoint de token de serviço é de baixa segurança interna
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Falha ao obter o Token de Serviço do AuthService.");
    }


    /**
     * Lógica principal de autenticação. Procura o utilizador em todos os serviços federados.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. OBTER O TOKEN DE SERVIÇO
        final String serviceToken = getServiceToken();

        // 2. CONSTRUIR O CABEÇALHO DE AUTORIZAÇÃO INTERNA
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + serviceToken);
        final HttpEntity<Void> entity = new HttpEntity<>(headers);

        UserInternalDTO userDto = null;

        // 3. LAÇO DE BUSCA SEQUENCIAL (Busca Federada)
        for (String baseUrl : this.userAuthSearchUrls) {
            try {
                // Endpoint interno protegido: /api/internal/users/{username}
                String url = baseUrl + "/api/internal/users/" + username;

                // Tenta a chamada HTTPS com o Token de Serviço
                ResponseEntity<UserInternalDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        UserInternalDTO.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    userDto = response.getBody();
                    break; // Encontrou o utilizador, para a busca.
                }

            } catch (HttpClientErrorException.NotFound ex) {
                // Usuário não encontrado neste serviço (continua para o próximo)
                System.out.println("Utilizador não encontrado em: " + baseUrl);
            } catch (Exception e) {
                // Erro de comunicação (log e continua para o próximo)
                System.err.println("Erro ao contactar serviço: " + baseUrl + " - " + e.getMessage());
            }
        }

        // 4. VERIFICAR RESULTADO FINAL
        if (userDto == null) {
            throw new UsernameNotFoundException("Utilizador '" + username + "' não encontrado em nenhum serviço federado.");
        }

        return new org.springframework.security.core.userdetails.User(
                userDto.username(),
                userDto.passwordHash(),
                mapRolesToAuthorities(userDto.roles())
        );
    }

    /**
     * Mapeia as Strings de Role (e.g., "ADMIN") para GrantedAuthority (e.g., "ROLE_ADMIN").
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /**
     * Contrato de dados interno (DTO) que os serviços de recurso devolvem.
     */
    public record UserInternalDTO(
            String id,
            String username,
            String passwordHash,
            Set<String> roles
    ) {}
}





