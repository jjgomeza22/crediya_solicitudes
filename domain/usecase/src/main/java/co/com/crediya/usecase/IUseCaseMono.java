package co.com.crediya.usecase;

import reactor.core.publisher.Mono;

public interface IUseCaseMono <I, O> {
    Mono<O> execute(I request);
}
