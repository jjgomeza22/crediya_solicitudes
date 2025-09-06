package co.com.crediya.usecase.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {
    public static <T>Mono<T> applicationNotFound(Integer id) {
        return Mono.error(new ApplicationNotFoundException(id));
    }
}
