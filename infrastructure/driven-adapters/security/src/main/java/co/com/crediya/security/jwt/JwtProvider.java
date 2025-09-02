package co.com.crediya.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getKey(secret)).build().parseSignedClaims(token).getPayload();
    }

    public String getSubject(String token) {
        return Jwts.parser().verifyWith(getKey(secret)).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(getKey(secret)).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (JwtException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey getKey(String secret) {
        byte[] secretByes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretByes);
    }
}
