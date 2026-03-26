package com.example.hotalproject.security.auth;

import com.example.hotalproject.security.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String email;
    private Role role;
}
