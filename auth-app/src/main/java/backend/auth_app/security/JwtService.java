package backend.auth_app.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import backend.auth_app.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;


@Service 
@Getter 
@Setter 
public class JwtService {
    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JwtService(
        @Value("${security.jwt.secret}")
        String secret,
        @Value("${security.jwt.access-ttl-seconds}")
        long accessTtlSeconds, 
        @Value ("${security.jwt.issuer}")
        String issuer, 
        @Value("${security.jwt.refresh-ttl-seconds}")
        long refreshTtlSeconds) {

        if(secret == null || secret.length() < 64) {
                throw new IllegalArgumentException("Secret key must not be null and must be at least 64 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
        this.issuer = issuer;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    // Generate Token
    public String generateAccessToken(User user){
        Instant now = Instant.now();
        List<String> roles = (user.getRoles() != null) ? user.getRoles().stream().map(role -> role.getName()).toList() : List.of();



        
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(
                    Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "type", "access"
                    )
                )
                .signWith(key, Jwts.SIG.HS512)
                .compact();
        
    }

    // Generate Refresh Token
    // jti is the unique identifier for the refresh token, which can be used to track and manage refresh tokens in your system.
    public String generateRefreshToken(User user, String jti){
        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTtlSeconds)))
                .claims(
                    Map.of(
                        "email", user.getEmail(),
                        "type", "refresh"
                    )
                )
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // Parse the token
    public Jws<Claims> parseToken(String token) {

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        
    }

    // Is Access Token
    public boolean isAccessToken(String token) {
        Claims c = parseToken(token).getPayload();
        return "access".equals(c.get("type"));
    }

    // Is Refresh Token
    public boolean isRefreshToken(String token) {
        Claims c = parseToken(token).getPayload();
        return "refresh".equals(c.get("type"));
    }

    public UUID getUserIdFromToken(String token) {
        Claims c = parseToken(token).getPayload();
        return UUID.fromString(c.getSubject());
    }

    public String getJtiFromToken(String token) {
        Claims c = parseToken(token).getPayload();
        return c.getId();
    }

    public String getEmailFromToken(String token) {
        Claims c = parseToken(token).getPayload();
        return c.get("email", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        Claims c = parseToken(token).getPayload();
        return (List<String>) c.get("roles");
    }

}
