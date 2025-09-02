package co.com.crediya.security.jwt;

import java.util.Collection;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class JwtAuthentication extends UsernamePasswordAuthenticationToken {

    private final String email;

    public JwtAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String email) {
        super(principal, credentials, authorities);
        this.email = email;
    }
}