package co.com.crediya.api;

import co.com.crediya.api.dto.SendLoanApplicationDto;
import co.com.crediya.api.exception.ApplicationExceptions;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.security.jwt.JwtAuthentication;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.loandaplicationtoreview.LoanApplicationToReviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final IUseCaseMono<LoanApplication, String> sendApplicationLoan;
    private final LoanApplicationToReviewUseCase loanApplicationToReviewUseCase;
    private final LoanApplicationMapper loanApplicationMapper;

    private static final String EVENT = "sendApplicationLoan";

    @PreAuthorize("hasAuthority('CLIENT')")
    public Mono<ServerResponse> sendApplicationLoan(ServerRequest request) {
        var endpoint = request.path();
        Log.logInfo(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.EXECUTED.name());
        return request.bodyToMono(SendLoanApplicationDto.class)
                .transform(RequestValidator.validate())
                .zipWith(ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .cast(JwtAuthentication.class)
                )
                .flatMap(this::validateCorrectEmail)
                .map(loanApplicationMapper::toModel)
                .flatMap(sendApplicationLoan::execute)
                .doOnNext(res -> Log.logInfo(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.FINALIZED.name()))
                .flatMap(ServerResponse.ok()::bodyValue)
                .doOnError(err -> Log.logError(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.ERROR.name(), new Exception(err)));
    }

    @PreAuthorize("hasAuthority('ADVISOR')")
    public Mono<ServerResponse> loanApplicationToReviewUseCase(ServerRequest request) {
        var limit = request.queryParam("limit").map(Integer::parseInt).orElse(1);
        var offset = request.queryParam("offset").map(Integer::parseInt).orElse(3);
        var states = Optional.ofNullable(request.queryParams().get("states")).orElse(Stream.of(StatesEnum.PE_REVIEW.toString()).toList());

        return this.loanApplicationToReviewUseCase.execute(limit, offset, states)
                .collectList()
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    private Mono<SendLoanApplicationDto> validateCorrectEmail(Tuple2<SendLoanApplicationDto, JwtAuthentication> tuple) {
        var loanApplication = tuple.getT1();
        var authentication = tuple.getT2();

        var email = authentication.getEmail();

        if (email.equals(loanApplication.email())) {
            return Mono.just(loanApplication);
        }

        return ApplicationExceptions.missingEmail();
    }
}
