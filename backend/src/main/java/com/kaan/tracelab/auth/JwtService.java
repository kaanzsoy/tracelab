package com.kaan.tracelab.auth;

import com.kaan.tracelab.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("tracelab")
                .issuedAt(now)
                .expiresAt(
                        now.plus(
                                expirationMinutes,
                                ChronoUnit.MINUTES
                        )
                )
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("fullName", user.getFullName())
                .claim(
                        "authorities",
                        List.of(
                                "ROLE_" + user.getRole().name()
                        )
                )
                .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(claims)
                )
                .getTokenValue();
    }
}