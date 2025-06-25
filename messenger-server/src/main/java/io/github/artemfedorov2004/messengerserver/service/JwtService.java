package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class JwtService {

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole());
        }
        return this.generateToken(claims, userDetails);
    }

    public String extractUsername(String token) throws JwtException, IllegalArgumentException {
        return this.extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !this.isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers)
            throws JwtException, IllegalArgumentException {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public abstract int getTtl();

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + getTtl()))
                .signWith(this.getSecretKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return this.extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return this.extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException {
        return Jwts.parser()
                .verifyWith(this.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public abstract String getSigningKey();

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.getSigningKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
