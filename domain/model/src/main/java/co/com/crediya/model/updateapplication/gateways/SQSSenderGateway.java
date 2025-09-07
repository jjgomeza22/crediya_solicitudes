package co.com.crediya.model.updateapplication.gateways;

import reactor.core.publisher.Mono;

public interface SQSSenderGateway {
    Mono<String> send(String message);
}
