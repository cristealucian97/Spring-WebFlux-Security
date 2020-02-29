package com.demo.reactive.application;

import com.demo.reactive.domain.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final MapReactiveUserDetailsService userDetails;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            Mono<UserDetails> userDetailsMono = userDetails.findByUsername(username);
            return userDetailsMono
                    .filter(Objects::nonNull)
                    .map(user -> new UsernamePasswordAuthenticationToken(user.getUsername(), token, user.getAuthorities()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
