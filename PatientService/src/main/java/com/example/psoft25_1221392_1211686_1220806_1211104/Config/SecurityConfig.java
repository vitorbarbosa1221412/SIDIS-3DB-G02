package com.example.psoft25_1221392_1211686_1220806_1211104.Config;

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // IMPORT NECESSÁRIO
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORT NECESSÁRIO
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.security.interfaces.RSAPublicKey;
import static java.lang.String.format;

/**
 * Configuração de Segurança para o PatientService.
 * Atua como Resource Server (valida JWTs) e não contém lógica de login local.
 */
@SuppressWarnings("removal")
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {


    // O PatientService só precisa da chave pública para validar tokens
    @Value("${jwt.public.key}")
    private RSAPublicKey rsaPublicKey;

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());

        // Permitir o uso de frames (necessário para H2 console)
        http.headers(headers -> headers.frameOptions().disable());

        // Set session management to stateless
        http = http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Set unauthorized requests exception handler
        http = http.exceptionHandling(
                exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

        // Set permissions on endpoints
        http.authorizeHttpRequests()
                // Swagger e H2 Console
                .requestMatchers("/").permitAll()
                .requestMatchers(format("%s/**", restApiDocPath)).permitAll()
                .requestMatchers(format("%s/**", swaggerPath)).permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Public endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/patients").permitAll()
                .requestMatchers("/api/registerPatient").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient/{patientNumber}").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient/search").permitAll()

                // NOVO: PROTEÇÃO DO ENDPOINT INTERNO (Para o AuthService ir buscar credenciais)
                .requestMatchers("/api/internal/**").hasRole("INTERNAL_SERVICE")
                // O PatientService deve gerir os seus próprios IDs
                .requestMatchers("/api/patients/id/**").permitAll()


                // Private endpoints Patient (Autorização baseada no JWT)
                .requestMatchers("/api/admin/user/**").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET,"/api/patients/{name}/profile").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET,"/api/patient/search/**").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET,"/api/patient/{year}/{id}/profile").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PUT,"/api/patients/updatePatient").hasRole(Role.PATIENT)

                // As regras restantes devem ser movidas para os respetivos microsserviços
                // Mantemos o resto das regras originais, mas corrigidas:

                .requestMatchers(HttpMethod.PUT,"/api/patients/{patientNumber}").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PATCH,"/api/patients/{patientNumber}").hasRole(Role.PHYSICIAN)

                //Appointments (Isto deve ser movido para o AppointmentsService, mas mantido por agora)
                .requestMatchers("/api/appointment").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PATCH, "/api/appointment/{appointmentNumber}/**").hasRole(Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/api/appointment/{appointmentNumber}/**").hasAnyRole(Role.PHYSICIAN, Role.PATIENT)
                .requestMatchers(HttpMethod.POST, "/api/appointment/patient/scheduleAppointment").hasAnyRole(Role.PATIENT, Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/average-duration").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/upcoming").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/monthly-report").hasRole(Role.ADMIN)

                //Physician (Isto deve ser movido para o PhysicianService, mas mantido por agora)
                .requestMatchers(HttpMethod.POST, "/api/physicians").hasAnyRole(Role.ADMIN, Role.PATIENT)
                .requestMatchers(HttpMethod.POST, "/{physicianNumber}").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/top5physicians").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/availableSlots").hasRole(Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/{id}").hasRole(Role.ADMIN)

                //AppointmentRecords (Isto deve ser movido para o AppointmentsService, mas mantido por agora)
                .requestMatchers(HttpMethod.POST, "/api/appointmentRecords").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/record/{recordNumber}").hasAnyRole(Role.ADMIN, Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/patient/{patientNumber}").hasAnyRole(Role.PHYSICIAN, Role.PATIENT)
                .requestMatchers(HttpMethod.PUT, "/{recordNumber}").hasAnyRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/search/{patientNumber}/{recordNumber}").hasAnyRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/getAll").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/electronic-prescription/{recordNumber}").hasRole(Role.PHYSICIAN)


                .anyRequest().authenticated()
                .and()
                // CONFIGURAÇÃO RESOURCE SERVER (Valida o JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );


        return http.build();
    }


    // Usado para descodificar e validar JWT tokens do AuthService
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }

    // Extract authorities from the roles claim
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
