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
import org.springframework.security.core.userdetails.UserDetails; // Import necessário

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/auth") // ALTERADO O PATH
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    // REMOVIDOS:
    // private final UserViewMapper userViewMapper;
    // private final UserService userService;

    @PostMapping("login")
    // O retorno é alterado para String (o JWT), pois UserView é desconhecido
    public ResponseEntity<String> login(@RequestBody @Valid final AuthRequest request) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));


            final UserDetails user = (UserDetails) authentication.getPrincipal();

            final Instant now = Instant.now();
            final long expiry = 3600L; // 1 hora ( 3600 segundos)

            final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));


            final String userIdPlaceholder = user.getUsername() + "_id";

            final JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("https://auth-service:8443") // ALTERADO para o domínio do AuthService
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(format("%s", user.getUsername())) // Usamos apenas o username/subject
                    .claim("roles", scope)
                    .claim("userId", userIdPlaceholder) // Placeholder. Deve ser o ID real
                    .build();

            final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            // Retornamos apenas o token no corpo e no cabeçalho
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(token);
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * signup to the service
     *
     * @param request
     * @return
     */
    //
    //
    /*
    @PostMapping("register")
    public UserView register(@RequestBody @Valid final CreateUserRequest request) {
        final var user = userService.create(request);
        return userViewMapper.toUserView(user);
    }
    */
};
