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

@SuppressWarnings("removal")
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.public.key}")
    private RSAPublicKey rsaPublicKey;

    // Mantida apenas para evitar erro de compilação, mas não é usada neste serviço.
    @Value("${jwt.private.key}")
    private String rsaPrivateKeyPlaceholder;

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    // =========================================================================
    // REINTRODUZIDO O BEAN PASSWORDENCODER PARA SATISFAZER O USER SERVICE
    // =========================================================================
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
                // Swagger endpoints
                .requestMatchers("/").permitAll()
                .requestMatchers(format("%s/**", restApiDocPath)).permitAll()
                .requestMatchers(format("%s/**", swaggerPath)).permitAll()

                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()

                // Public endpoints
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/patients").permitAll() //WP2B
                .requestMatchers("/api/registerPatient").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient/{patientNumber}").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/patient/search").permitAll()

                // ADIÇÃO CRÍTICA: PROTEGER O ENDPOINT INTERNO
                .requestMatchers("/api/internal/**").hasRole("INTERNAL_SERVICE")

                // Private endpoints Patient (Autorização baseada no JWT)
                .requestMatchers("/api/admin/user/**").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET,"/api/patients/{name}/profile").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET,"/api/patient/search/**").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET,"/api/patient/{year}/{id}/profile").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PUT,"/api/patients/updatePatient").hasRole(Role.PATIENT)

                .requestMatchers(HttpMethod.PUT,"/api/patients/{patientNumber}").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PATCH,"/api/patients/{patientNumber}").hasRole(Role.PHYSICIAN)

                //Appointments
                .requestMatchers("/api/appointment").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.PATCH, "/api/appointment/{appointmentNumber}/**").hasRole(Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/api/appointment/{appointmentNumber}/**").hasAnyRole(Role.PHYSICIAN, Role.PATIENT)
                .requestMatchers(HttpMethod.POST, "/api/appointment/patient/scheduleAppointment").hasAnyRole(Role.PATIENT, Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/average-duration").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/upcoming").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/appointment/monthly-report").hasRole(Role.ADMIN)

                //Physician
                .requestMatchers(HttpMethod.POST, "/api/physicians").hasAnyRole(Role.ADMIN, Role.PATIENT)
                .requestMatchers(HttpMethod.POST, "/{physicianNumber}").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/top5physicians").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/availableSlots").hasRole(Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/{id}").hasRole(Role.ADMIN)

                //AppointmentRecords
                .requestMatchers(HttpMethod.POST, "/api/appointmentRecords").hasRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/record/{recordNumber}").hasAnyRole(Role.ADMIN, Role.PATIENT)
                .requestMatchers(HttpMethod.GET, "/patient/{patientNumber}").hasAnyRole(Role.PHYSICIAN, Role.PATIENT)
                .requestMatchers(HttpMethod.PUT, "/{recordNumber}").hasAnyRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/search/{patientNumber}/{recordNumber}").hasAnyRole(Role.PHYSICIAN)
                .requestMatchers(HttpMethod.GET, "/getAll").hasRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/electronic-prescription/{recordNumber}").hasRole(Role.PHYSICIAN)

                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );


        return http.build();
    }


    // MANTIDO: Usado para descodificar e validar JWT tokens do AuthService
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }

    // MANTIDO: Extrai autoridades (roles) do claim 'roles' do JWT
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // MANTIDO: Usado por spring security se CORS for ativado.
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

