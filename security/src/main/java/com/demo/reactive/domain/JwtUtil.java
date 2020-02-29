package com.demo.reactive.domain;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtil {

    private static final int TOKEN_EXPIRATION_TIME = 100000;
    private static final String SECRET_KEY = "MyRandomSuperSecretKey";

    public String createToken(UserDetails details) {
        Date currentDate = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setSubject(details.getUsername())
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                .setIssuedAt(currentDate)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .compact();
    }

    public String getUsernameFromToken(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(getBase64EncodedKeyBytes())
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }

    private String getBase64EncodedKeyBytes() {
        return Base64.getEncoder()
                .encodeToString(SECRET_KEY.getBytes());
    }
}
