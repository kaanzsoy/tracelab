package com.kaan.tracelab.auth;

import com.kaan.tracelab.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(
                min = 3,
                max = 100,
                message = "Username must be between 3 and 100 characters"
        )
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(
                min = 8,
                max = 100,
                message = "Password must be between 8 and 100 characters"
        )
        String password,

        @NotBlank(message = "Full name cannot be blank")
        @Size(
                max = 150,
                message = "Full name must be at most 150 characters"
        )
        String fullName

        /* 
        @NotNull(message = "Role is required")
        Role role
        */
) {
}