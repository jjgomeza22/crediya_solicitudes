package co.com.crediya.security.jwt;

import co.com.crediya.security.exception.InvalidAuthException;
import co.com.crediya.utils.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class JwtFilter implements WebFilter {
    @Value("${cors.allowed-paths}")
    private String paths;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (Stream.of(paths.split(",")).anyMatch(path::contains)) {
            return chain.filter(exchange);
        }

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(auth)) {
            return Mono.error(new InvalidAuthException("No token was found"));
        }

        if (!auth.startsWith("Bearer ")) {
            return Mono.error(new InvalidAuthException("Token is missing or invalid"));
        }

        String token = auth.replace(Constants.BEARER + " ", "");

        exchange.getAttributes().put(Constants.TOKEN, token);
        return chain.filter(exchange);


    }
}
