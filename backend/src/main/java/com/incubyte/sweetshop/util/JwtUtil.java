package com.incubyte.sweetshop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT UTILITY - Creates and validates authentication tokens
 * 
 * JWT (JSON Web Token) is like a special ticket that:
 * - Proves the user is logged in
 * - Contains encrypted user information
 * - Has an expiration date (tickets expire)
 * - Cannot be forged (uses secret key)
 * 
 * This utility handles:
 * - Creating new tokens when user logs in
 * - Reading information from tokens
 * - Checking if token is expired
 * - Validating token is real (not fake)
 */
@Component  // Spring will create one instance of this utility
public class JwtUtil {
    // Secret key used to encrypt/decrypt tokens (loaded from configuration)
    @Value("${jwt.secret}")
    private String secret;

    // How long token is valid (in milliseconds)
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * GET SIGNING KEY - Creates the encryption key
     * This key is used to sign (encrypt) tokens so they can't be forged
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * GENERATE TOKEN - Create a new authentication token for a user
     * 
     * What the token contains:
     * - username: Who this token belongs to
     * - issued at: When it was created
     * - expiration: When it expires
     * - signature: Encrypted proof it's real
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)  // Set the username in the token
                .issuedAt(new Date())  // Set creation time
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Set expiration
                .signWith(getSigningKey())  // Encrypt with secret key
                .compact();  // Convert to string token
    }

    /**
     * EXTRACT USERNAME - Get the username from a token
     * Opens the token and reads the username stored inside
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * EXTRACT EXPIRATION - Get when the token expires
     * Opens the token and reads the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
}

