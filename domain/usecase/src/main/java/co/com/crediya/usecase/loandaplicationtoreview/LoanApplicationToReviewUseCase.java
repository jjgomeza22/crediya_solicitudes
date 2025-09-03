package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.usecase.sendapplicationloan.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class LoanApplicationToReviewUseCase {
    private final LoanApplicationRepository loanApplicationRepository;

    public Flux<LoanDetails> execute(Integer limit, Integer offset, List<String> states) {
        return Flux.fromIterable(states)
                .flatMap(state -> Mono.just(StatesEnum.valueOf(state).getStateId()))
                .collectList()
                .flatMapMany(statesIdList -> this.loanApplicationRepository
                        .getPendingLoanApplications(limit, offset, statesIdList)
                )
                .onErrorMap(e -> new InvalidInputException("Invalid state"));
    }

}
