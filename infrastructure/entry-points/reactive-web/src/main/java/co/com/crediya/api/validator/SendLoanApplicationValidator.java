package co.com.crediya.api.validator;

import co.com.crediya.api.dto.SendLoanApplicationDto;
import co.com.crediya.api.exception.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SendLoanApplicationValidator {
    public static UnaryOperator<Mono<SendLoanApplicationDto>> validate() {
        return mono -> mono
                .filter(missingAmount())
                .switchIfEmpty(ApplicationExceptions.missingAmount())
                .filter(missingTimeLimit())
                .switchIfEmpty(ApplicationExceptions.missingTimeLimit())
                .filter(missingEmail())
                .switchIfEmpty(ApplicationExceptions.missingEmail())
                .filter(missingLoanType())
                .switchIfEmpty(ApplicationExceptions.missingLoanType());
    }

    private static Predicate<SendLoanApplicationDto> missingAmount() {
        return dto -> Objects.nonNull(dto.amount());
    }

    private static Predicate<SendLoanApplicationDto> missingTimeLimit() {
        return dto -> Objects.nonNull(dto.timeLimit());
    }

    private static Predicate<SendLoanApplicationDto> missingEmail() {
        return dto -> Objects.nonNull(dto.email());
    }

    private static Predicate<SendLoanApplicationDto> missingLoanType() {
        return dto -> Objects.nonNull(dto.loanTypeId());
    }
}
