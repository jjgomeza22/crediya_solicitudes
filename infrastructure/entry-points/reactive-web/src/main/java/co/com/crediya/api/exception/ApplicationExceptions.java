package co.com.crediya.api.exception;

import co.com.crediya.usecase.sendapplicationloan.exception.InvalidInputException;
import reactor.core.publisher.Mono;

public class ApplicationExceptions {
    public static <T> Mono<T> missingAmount() {
        return Mono.error(new InvalidInputException("Missing Amount"));
    }

    public static <T> Mono<T> missingTimeLimit() {
        return Mono.error(new InvalidInputException("Missing TimeLimit"));
    }

    public static <T> Mono<T> missingEmail() {
        return Mono.error(new InvalidInputException("Missing or incorrect Email"));
    }

    public static <T> Mono<T> missingLoanType() {
        return Mono.error(new InvalidInputException("Missing LoanType"));
    }
}
