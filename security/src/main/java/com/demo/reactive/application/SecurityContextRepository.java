package com.demo.reactive.application;

import com.demo.reactive.domain.TokenEntityDto;
import com.demo.reactive.infrastructure.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        return Mono.error(new UnsupportedOperationException());
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        String authorizationToken = extractAuthorizationToken(swe);
        if (authorizationToken != null) {
            TokenEntityDto tokenEntity = tokenRepository.findByValue(authorizationToken);
            if (tokenEntity != null) {
                return createSecurityContext(tokenEntity.getValue());
            }
        }
        return Mono.empty();
    }

    private Mono<SecurityContext> createSecurityContext(String token) {
        Authentication auth = new UsernamePasswordAuthenticationToken(token, null, null);
        return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
    }

    private String extractAuthorizationToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String tokenPrefix = String.format("%s ", "Bearer");
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            return authHeader.replace(tokenPrefix, "");
        } else {
            return null;
        }
    }
}
