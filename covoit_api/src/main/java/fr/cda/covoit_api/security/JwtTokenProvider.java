package fr.cda.covoit_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Composant utilitaire pour la génération et la validation des tokens JWT.
 * Utilise l'algorithme HS256 avec une clé secrète configurée.
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:}")
    private String JWT_SECRET;
    private final long JWT_EXPIRATION = 86400000L; // 24h

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    /**
     * Génère un token JWT signé pour un email utilisateur donné.
     *
     * @param email Sujet (Subject) du token.
     * @return Chaîne de caractères représentant le JWT.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valide l'intégrité et l'expiration d'un token.
     *
     * @param token Le JWT à vérifier.
     * @return true si le token est valide, false en cas d'erreur de signature ou d'expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}