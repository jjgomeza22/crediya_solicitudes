package co.com.crediya.consumer.client;

import co.com.crediya.log.Log;
import co.com.crediya.model.loandetails.gateways.UsersByEmailGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.security.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class AuthenticationServiceClient implements UsersByEmailGateway {
    private final WebClient webClient;

    @Override
    public Mono<List<UserByEmailDto>> getUsersInformation(String emails) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthentication.class)
                .map(JwtAuthentication::getToken)
                .flatMap(token -> this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/usuarios")
                                .queryParam("emails", emails)
                                .build()
                        )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToFlux(UserByEmailDto.class)
                        .collectList()
                        .map(data -> data)
                        .doOnError(e -> Log.logError("", "", "", new Exception(e)))
                )
                .doOnError(e -> Log.logError("", "", "", new Exception(e)));
    }
}
