package co.com.crediya.model.updateapplication.gateways;

import reactor.core.publisher.Mono;

public interface SQSSenderGateway {
    Mono<String> sendEmailQueue(String message);
    Mono<String> sendDebtCapacityQueue(String message);
}
