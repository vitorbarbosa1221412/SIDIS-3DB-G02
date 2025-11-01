package com.example.psoft25_1221392_1211686_1220806_1211104.auth.api;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth")
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    // Método de Login: Autentica o utilizador e emite um JWT
    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody @Valid final AuthRequest request) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));


            final UserDetails user = (UserDetails) authentication.getPrincipal();

            final Instant now = Instant.now();
            final long expiry = 3600L; // 1 hora de validade para o token de utilizador

            final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));


            final String userIdPlaceholder = user.getUsername() + "_id";

            final JwtClaimsSet claims = JwtClaimsSet.builder()
                    // CRÍTICO: Emissor consistente com a configuração de teste local
                    .issuer("https://localhost:8443")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(format("%s", user.getUsername()))
                    .claim("roles", scope)
                    .claim("userId", userIdPlaceholder)
                    .build();

            final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            // Retornamos o token no corpo e no cabeçalho
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(token);

        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Endpoint interno para o RemoteUserDetailsService.
     * Emite um JWT interno com a role INTERNAL_SERVICE para uso entre microsserviços.
     */
    @PostMapping("service-token")
    public ResponseEntity<String> getInternalServiceToken() {

        final String serviceUsername = "hap-auth-service";
        final String serviceRole = "INTERNAL_SERVICE";

        final Instant now = Instant.now();
        final long expiry = 300L; // 5 minutos de validade para o token de serviço

        final String scope = serviceRole;

        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("https://localhost:8443") // Emissor consistente
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(serviceUsername)
                .claim("roles", scope)
                .claim("service_id", serviceUsername)
                .build();

        final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(token);
    }

    /*
    @PostMapping("register")
    public UserView register(@RequestBody @Valid final CreateUserRequest request) {
        // ... Lógica de registo movida para o Patient/PhysicianService
    }
    */
}
