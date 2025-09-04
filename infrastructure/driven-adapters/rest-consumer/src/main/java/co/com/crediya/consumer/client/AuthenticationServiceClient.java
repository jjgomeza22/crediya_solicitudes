package co.com.crediya.consumer.client;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loandetails.gateways.UsersByEmailGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.security.exception.InvalidAuthException;
import co.com.crediya.security.jwt.JwtAuthentication;
import co.com.crediya.utils.constants.Constants;
import co.com.crediya.utils.constants.Method;
import co.com.crediya.utils.constants.Param;
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
    private String usersPath = "/usuarios";

    @Override
    public Mono<List<UserByEmailDto>> getUsersInformation(String emails) {
        var method = Method.GET_USER_INFORMATION;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthentication.class)
                .map(JwtAuthentication::getToken)
                .flatMap(token -> this.webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(usersPath)
                                .queryParam(Param.EMAILS, emails)
                                .build()
                        )
                        .header(HttpHeaders.AUTHORIZATION, Constants.BEARER + " " + token)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToFlux(UserByEmailDto.class)
                        .collectList()
                        .onErrorMap(e -> new InvalidAuthException(e.getMessage()))
                )
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }
}
