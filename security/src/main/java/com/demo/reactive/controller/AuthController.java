package com.demo.reactive.controller;

import com.demo.reactive.domain.JwtUtil;
import com.demo.reactive.domain.TokenEntityDto;
import com.demo.reactive.domain.UserDto;
import com.demo.reactive.infrastructure.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final TokenRepository tokenRepository;
    private final ReactiveUserDetailsService userDetailsService;

    @PostMapping(path = "/auth/token")
    public Mono<ResponseEntity<String>> authorize(@Valid @RequestBody UserDto user) {
        return userDetailsService.findByUsername(user.getUsername())
                .filter(Objects::nonNull)
                .filter(userDetails -> encoder.matches(user.getPassword(), userDetails.getPassword()))
                .map(this::createAuthorizationToken)
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password"));
    }

    private String createAuthorizationToken(UserDetails userDetails) {
        String token = jwtUtil.createToken(userDetails);
        tokenRepository.save(new TokenEntityDto(token));
        return token;
    }
}
