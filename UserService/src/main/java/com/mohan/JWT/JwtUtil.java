package com.mohan.JWT;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour in milliseconds

    /**
     * Generates a JWT token for a given username.
     *
     * @param username The username for which to generate the token.
     * @return A JWT token as a String.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Extracts the username from the given token.
     *
     * @param token The JWT token.
     * @return The username embedded in the token.
     */
    public String extractUsername(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (JwtException e) {
            System.err.println("Error extracting username from token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates the given JWT token.
     *
     * @param token The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !isTokenExpired(claims);
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves the authentication object from the token.
     *
     * @param token               The JWT token.
     * @param userDetailsService  The UserDetailsService to load user details.
     * @return An Authentication object if the token is valid, null otherwise.
     */
    public Authentication getAuthentication(String token, UserDetailsService userDetailsService) {
        String username = extractUsername(token);
        if (username == null) {
            return null;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * Extracts claims from the token.
     *
     * @param token The JWT token.
     * @return The claims extracted from the token.
     * @throws JwtException if the token is invalid.
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.err.println("Token has expired: " + e.getMessage());
            throw e;
        } catch (JwtException e) {
            System.err.println("Error parsing token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if the token is expired.
     *
     * @param claims The claims extracted from the token.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
