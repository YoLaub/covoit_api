package fr.cda.covoit_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        // Injecter une clé secrète de test (min 32 caractères pour HS256)
        ReflectionTestUtils.setField(tokenProvider, "JWT_SECRET",
                "test-secret-key-minimum-32-chars-long!");
    }

    // ============================================================
    // generateToken
    // Réf: JwtTokenProvider.java → generateToken()
    // ============================================================
    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = tokenProvider.generateToken("user@test.com");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void generateToken_ShouldReturnDifferentTokensForDifferentEmails() {
        String token1 = tokenProvider.generateToken("user1@test.com");
        String token2 = tokenProvider.generateToken("user2@test.com");

        assertThat(token1).isNotEqualTo(token2);
    }

    // ============================================================
    // getEmailFromToken
    // Réf: JwtTokenProvider.java → getEmailFromToken()
    // ============================================================
    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        String token = tokenProvider.generateToken("user@test.com");

        String email = tokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("user@test.com");
    }

    // ============================================================
    // validateToken
    // Réf: JwtTokenProvider.java → validateToken()
    // ============================================================
    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        String token = tokenProvider.generateToken("user@test.com");

        assertThat(tokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        assertThat(tokenProvider.validateToken("invalid.token.here")).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsNull() {
        assertThat(tokenProvider.validateToken(null)).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsEmpty() {
        assertThat(tokenProvider.validateToken("")).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsTampered() {
        String token = tokenProvider.generateToken("user@test.com");
        // Altérer le token
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(tokenProvider.validateToken(tampered)).isFalse();
    }
}