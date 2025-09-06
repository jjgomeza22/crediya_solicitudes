package co.com.crediya.api;

import co.com.crediya.api.dto.SendLoanApplicationDto;
import co.com.crediya.api.dto.UpdateApplicationStateDTO;
import co.com.crediya.api.exception.ApplicationExceptions;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.UpdateApplication;
import co.com.crediya.security.jwt.JwtAuthentication;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.loandaplicationtoreview.LoanApplicationToReviewUseCase;
import co.com.crediya.usecase.updateloanapplicationstate.UpdateLoanApplicationStateUseCase;
import co.com.crediya.utils.constants.Event;
import co.com.crediya.utils.constants.Param;
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

    private final IUseCaseMono<LoanApplication, String> sendLoanApplication;
    private final LoanApplicationToReviewUseCase loanApplicationToReviewUseCase;
    private final UpdateLoanApplicationStateUseCase updateLoanApplicationStateUseCase;
    private final LoanApplicationMapper loanApplicationMapper;

    @PreAuthorize("hasAuthority('CLIENT')")
    public Mono<ServerResponse> sendLoanApplication(ServerRequest request) {
        var endpoint = request.path();
        Log.logInfo(Event.SEND_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.EXECUTED.name());
        return request.bodyToMono(SendLoanApplicationDto.class)
                .transform(RequestValidator.validate())
                .zipWith(ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication)
                        .cast(JwtAuthentication.class)
                )
                .flatMap(this::validateCorrectEmail)
                .map(loanApplicationMapper::toModel)
                .flatMap(sendLoanApplication::execute)
                .doOnNext(res -> Log.logInfo(Event.SEND_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.FINALIZED.name()))
                .flatMap(ServerResponse.ok()::bodyValue)
                .doOnError(err -> Log.logError(Event.SEND_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.ERROR.name(), new Exception(err)));
    }

    @PreAuthorize("hasAuthority('ADVISOR')")
    public Mono<ServerResponse> loanApplicationToReview(ServerRequest request) {
        var limit = request.queryParam(Param.LIMIT).map(Integer::parseInt).orElse(1);
        var offset = request.queryParam(Param.OFFSET).map(Integer::parseInt).orElse(0);
        var states = Optional.ofNullable(request.queryParams().get(Param.STATES)).orElse(Stream.of(StatesEnum.PE_REVIEW.toString()).toList());

        var endpoint = request.path();
        Log.logInfo(Event.LOAN_APPLICATION_TO_REVIEW, this.getClass().getCanonicalName().concat(endpoint), Status.EXECUTED.name());
        return this.loanApplicationToReviewUseCase.execute(limit, offset, states)
                .collectList()
                .doOnNext(res -> Log.logInfo(Event.LOAN_APPLICATION_TO_REVIEW, this.getClass().getCanonicalName().concat(endpoint), Status.FINALIZED.name()))
                .flatMap(ServerResponse.ok()::bodyValue)
                .doOnError(err -> Log.logError(Event.LOAN_APPLICATION_TO_REVIEW, this.getClass().getCanonicalName().concat(endpoint), Status.ERROR.name(), new Exception(err)));
    }

    @PreAuthorize("hasAuthority('ADVISOR')")
    public Mono<ServerResponse> approvedOrDeclineLoanApplication(ServerRequest request) {
        var applicationId = Integer.parseInt(request.pathVariable("id"));

        var endpoint = request.path();
        Log.logInfo(Event.APPROVED_OR_DECLINE_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.EXECUTED.name());
        return request.bodyToMono(UpdateApplicationStateDTO.class)
                .flatMap(mono -> this.updateLoanApplicationStateUseCase.execute(new UpdateApplication(
                                applicationId, mono.state()
                        ))
                )
                .doOnNext(res -> Log.logInfo(Event.APPROVED_OR_DECLINE_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.FINALIZED.name()))
                .flatMap(ServerResponse.ok()::bodyValue)
                .doOnError(err -> Log.logError(Event.APPROVED_OR_DECLINE_LOAN_APPLICATION, this.getClass().getCanonicalName().concat(endpoint), Status.ERROR.name(), new Exception(err)));
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
