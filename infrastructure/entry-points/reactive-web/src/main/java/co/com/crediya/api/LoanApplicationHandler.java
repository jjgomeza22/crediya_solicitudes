package co.com.crediya.api;

import co.com.crediya.api.dto.SendLoanApplicationDto;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.usecase.IUseCaseMono;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final IUseCaseMono<LoanApplication, String> sendApplicationLoan;
    private final LoanApplicationMapper loanApplicationMapper;

    private static final String EVENT = "sendApplicationLoan";

    @PreAuthorize("hasAuthority('CLIENT')")
    public Mono<ServerResponse> sendApplicationLoan(ServerRequest request) {
        var endpoint = request.path();
        Log.logInfo(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.EXECUTED.name());
        return request.bodyToMono(SendLoanApplicationDto.class)
                .transform(RequestValidator.validate())
                .map(loanApplicationMapper::toModel)
                .flatMap(sendApplicationLoan::execute)
                .doOnNext(res -> Log.logInfo(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.FINALIZED.name()))
                .flatMap(ServerResponse.ok()::bodyValue)
                .doOnError(err -> Log.logError(EVENT, this.getClass().getCanonicalName().concat(endpoint), Status.ERROR.name(), new Exception(err)));
    }
}
