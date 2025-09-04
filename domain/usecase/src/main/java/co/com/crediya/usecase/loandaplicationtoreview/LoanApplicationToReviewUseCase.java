package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.loandetails.gateways.UsersByEmailGateway;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.usecase.sendapplicationloan.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class LoanApplicationToReviewUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final UsersByEmailGateway usersByEmailGateway;

    public Flux<LoanDetails> execute(Integer limit, Integer offset, List<String> states) {
        return this.usersByEmailGateway.getUsersInformation("client@mail.com")
                .flatMapMany(data -> Flux.fromIterable(states))
                .flatMap(state -> Mono.just(StatesEnum.valueOf(state).getStateId()))
                .onErrorResume(IllegalArgumentException.class, ex -> Mono.error(new InvalidInputException("Invalid state")))
                .collectList()
                .flatMapMany(statesIdList -> this.loanApplicationRepository
                        .getPendingLoanApplications(limit, offset, statesIdList)
                );
    }

}
