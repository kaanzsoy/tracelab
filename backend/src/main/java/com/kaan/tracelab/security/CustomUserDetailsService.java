package com.kaan.tracelab.security;

import com.kaan.tracelab.user.User;
import com.kaan.tracelab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username) // PostgreSQL'dan bul
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        )
                );
        
        // Spring Security'nin anlayacagi UserDetails formatina donusturuyoruz
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())   // ornegin TESTER rolunu Spring Security tarafi ROLE_TESTER authoritysine donusturur
                .disabled(!user.isEnabled())
                .build();
    }
}