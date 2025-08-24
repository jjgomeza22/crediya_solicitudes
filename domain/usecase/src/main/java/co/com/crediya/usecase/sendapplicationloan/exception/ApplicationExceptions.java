package co.com.crediya.usecase.sendapplicationloan.exception;

import reactor.core.publisher.Mono;

public class ApplicationExceptions {
    public static <T>Mono<T> loanTypeNotFound() {
        return Mono.error(new LoanTypeNotFoundException("Loan type doesn't exists"));
    }
}
