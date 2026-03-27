package com.example.hotalproject.security.auth;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.ConflictException;
import com.example.hotalproject.security.AppUser;
import com.example.hotalproject.security.AppUserRepository;
import com.example.hotalproject.security.JwtService;
import com.example.hotalproject.security.Role;
import com.example.hotalproject.security.refresh.RefreshToken;
import com.example.hotalproject.security.refresh.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User already exists with email: " + request.getEmail());
        }

        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() == null ? Role.GUEST : request.getRole())
                .build();

        AppUser saved = userRepository.save(user);
        refreshTokenService.revokeAllUserTokens(saved);
        RefreshToken refreshToken = refreshTokenService.issueToken(saved);
        return buildAuthResponse(saved, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid email or password");
        }

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        refreshTokenService.revokeAllUserTokens(user);
        RefreshToken refreshToken = refreshTokenService.issueToken(user);
        return buildAuthResponse(user, refreshToken.getToken());
    }

    @Transactional
    public refrechResponse refresh(RefreshTokenRequest request) {
        RefreshToken rotatedToken = refreshTokenService.verifyAndRotate(request.getRefreshToken());
        return buildrefrechResponse(rotatedToken.getUser(), rotatedToken.getToken());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeByToken(request.getRefreshToken());
    }

    private AuthResponse buildAuthResponse(AppUser user, String refreshToken) {
        return AuthResponse.builder()

                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getRefreshTokenExpirationMs())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    private refrechResponse buildrefrechResponse(AppUser user, String refreshToken) {
        return refrechResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationMs())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
