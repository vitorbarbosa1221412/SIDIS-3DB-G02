package com.example.psoft25_1221392_1211686_1220806_1211104.Config;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.Role;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.repositories.UserRepository;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Collections;

import static java.lang.String.format;

/**
 * Check https://www.baeldung.com/security-spring and
 * https://www.toptal.com/spring/spring-security-tutorial
 * <p>
 * Based on https://github.com/Yoh0xFF/java-spring-security-example/
 *
 * @author pagsousa
 *
 */
@SuppressWarnings("removal")
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepo;

    @Value("${jwt.public.key}")
    private RSAPublicKey rsaPublicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey rsaPrivateKey;

    @Value("${springdoc.api-docs.path}")
    private String restApiDocPath;

    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;

    @Bean
    public AuthenticationManager authenticationManager(final UserDetailsService userDetailsService,
                                                       final PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(format("User: %s, not found", username)));
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable());

        // 👇 Permitir o uso de frames (necessário para H2 console)
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

                // Private endpoints Patient
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
                .requestMatchers(HttpMethod.GET, "/getAll").hasAnyRole(Role.ADMIN)
                .requestMatchers(HttpMethod.GET, "/electronic-prescription/{recordNumber}").hasAnyRole(Role.PHYSICIAN)

                .anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );


        return http.build();
    }


    // Used by JwtAuthenticationProvider to generate JWT tokens
    @Bean
    public JwtEncoder jwtEncoder() {
        final JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
        final JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    // Used by JwtAuthenticationProvider to decode and validate JWT tokens
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


    // Set password encoding schema
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
