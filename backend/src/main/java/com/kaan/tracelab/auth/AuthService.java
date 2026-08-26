package com.kaan.tracelab.auth;

import com.kaan.tracelab.user.User;
import com.kaan.tracelab.user.UserRepository;
import com.kaan.tracelab.user.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "Username is already in use"
            );
        }

        User user = User.builder()
                .username(request.username())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )   // sifre hashlenir
                .fullName(request.fullName())
                //.role(request.role())
                .role(Role.TESTER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return createAuthResponse(savedUser, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        /*
        CustomUserDetailsService ile kullanici bulunur
        Girilen plaintext password alinir ve db'deki BCrypt hash ile karsilastirilir
        dogruysa authenticate basarili, yanlissa exception
        */

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid username or password"
                        )
                );

        String token = jwtService.generateToken(user);

        return createAuthResponse(user, token);
    }

    private AuthResponse createAuthResponse(
            User user,
            String token
    ) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole()
        );
    }
}