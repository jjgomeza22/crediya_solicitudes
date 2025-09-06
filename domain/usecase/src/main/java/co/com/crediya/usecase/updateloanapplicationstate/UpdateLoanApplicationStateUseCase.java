package co.com.crediya.usecase.updateloanapplicationstate;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.UpdateApplication;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateLoanApplicationStateUseCase implements IUseCaseMono<UpdateApplication, String> {
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public Mono<String> execute(UpdateApplication request) {
        return loanApplicationRepository.findApplicationById(request.getId())
                .switchIfEmpty(ApplicationExceptions.applicationNotFound(request.getId()))
                .doOnNext(la -> la.setStateId(StatesEnum.valueOf(request.getState().toString()).getStateId()))
                .flatMap(loanApplicationRepository::saveLoanApplication);
    }
}
