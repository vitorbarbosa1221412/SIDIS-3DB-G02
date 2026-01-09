package com.example.psoft25_1221392_1211686_1220806_1211104.authApi;

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

import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.api.UserView;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.api.UserViewMapper;
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.model.User;
// REMOVIDO: import CreateUserRequest (Escrita)
import com.example.psoft25_1221392_1211686_1220806_1211104.usermanagement.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/public")
public class AuthApi {

    private final AuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    private final UserViewMapper userViewMapper;

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid final AuthRequest request) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            final User user = (User) authentication.getPrincipal();

            final Instant now = Instant.now();
            final long expiry = 36000L; // 1 hora

            final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));

            final JwtClaimsSet claims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", user.getId(), user.getUsername()))
                    .claim("roles", scope)
                    .claim("userId", user.getId()).build();

            final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(userViewMapper.toUserView(user));
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
