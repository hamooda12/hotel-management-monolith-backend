package com.example.hotalproject.security.refresh;

import com.example.hotalproject.security.AppUser;
import com.example.hotalproject.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void verifyAndRotate_shouldRevokeOldAndIssueNewToken() {
        AppUser user = AppUser.builder().id(1L).email("guest@test.com").role(Role.GUEST).build();
        RefreshToken existing = RefreshToken.builder()
                .id(10L)
                .token("old")
                .user(user)
                .revoked(false)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 604800000L);
        when(refreshTokenRepository.findByToken("old")).thenReturn(Optional.of(existing));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken rotated = refreshTokenService.verifyAndRotate("old");

        assertThat(existing.isRevoked()).isTrue();
        assertThat(rotated.getToken()).isNotBlank();
        assertThat(rotated.getUser()).isEqualTo(user);
        verify(refreshTokenRepository, atLeast(2)).save(any(RefreshToken.class));
    }

    @Test
    void verifyAndRotate_shouldThrowWhenExpired() {
        RefreshToken expired = RefreshToken.builder()
                .token("expired")
                .revoked(false)
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .build();

        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(expired));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expired);

        assertThatThrownBy(() -> refreshTokenService.verifyAndRotate("expired"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("expired");
        assertThat(expired.isRevoked()).isTrue();
    }

    @Test
    void revokeAllUserTokens_shouldMarkAllActiveTokensRevoked() {
        AppUser user = AppUser.builder().id(2L).email("u@test.com").role(Role.GUEST).build();
        RefreshToken token1 = RefreshToken.builder().token("a").user(user).revoked(false).expiryDate(LocalDateTime.now().plusDays(1)).build();
        RefreshToken token2 = RefreshToken.builder().token("b").user(user).revoked(false).expiryDate(LocalDateTime.now().plusDays(1)).build();

        when(refreshTokenRepository.findByUserAndRevokedFalse(user)).thenReturn(List.of(token1, token2));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        refreshTokenService.revokeAllUserTokens(user);

        assertThat(token1.isRevoked()).isTrue();
        assertThat(token2.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }
}
