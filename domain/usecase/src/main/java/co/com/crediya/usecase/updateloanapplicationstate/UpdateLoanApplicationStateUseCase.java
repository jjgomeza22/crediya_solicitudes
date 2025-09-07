package co.com.crediya.usecase.updateloanapplicationstate;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.UpdateApplication;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RequiredArgsConstructor
public class UpdateLoanApplicationStateUseCase implements IUseCaseMono<UpdateApplication, String> {
    private final LoanApplicationRepository loanApplicationRepository;
    private final SQSSenderGateway sqsSenderGateway;

    @Override
    public Mono<String> execute(UpdateApplication request) {
        return loanApplicationRepository.findApplicationById(request.getId())
                .switchIfEmpty(ApplicationExceptions.applicationNotFound(request.getId()))
                .doOnNext(la -> la.setStateId(StatesEnum.valueOf(request.getState().toString()).getStateId()))
                .flatMap(la -> loanApplicationRepository.saveLoanApplication(la)
                        .zipWith(sendSqsMessage(la.getEmail(), request.getState()))
                )
                .map(Tuple2::getT1);
    }

    private Mono<String> sendSqsMessage(String email, StatesEnum state) {
        String message = String.format(
                "{\"email\": \"%s\", \"state\": \"%s\"}",
                email,
                state
        );
        return sqsSenderGateway.send(message);
    }
}
