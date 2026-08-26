package com.kaan.tracelab.auth;

import com.kaan.tracelab.user.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String username,
        String fullName,
        Role role
) {
}