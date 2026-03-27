package com.example.hotalproject.security.refresh;

import com.example.hotalproject.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.security.jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    @Transactional
    public RefreshToken issueToken(AppUser user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID() + "." + UUID.randomUUID())
                .user(user)
                .expiryDate(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyAndRotate(String token) {
        RefreshToken current = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid"));

        if (current.isRevoked()) {
            throw new BadCredentialsException("Refresh token is revoked");
        }

        if (current.getExpiryDate().isBefore(LocalDateTime.now())) {
            current.setRevoked(true);
            refreshTokenRepository.save(current);
            throw new BadCredentialsException("Refresh token is expired");
        }

        current.setRevoked(true);
        refreshTokenRepository.save(current);
        return issueToken(current.getUser());
    }

    @Transactional
    public void revokeByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(AppUser user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(existing -> {
            existing.setRevoked(true);
            refreshTokenRepository.save(existing);
        });
    }
}
