package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class LoanApplicationToReviewUseCase {
    private final LoanApplicationRepository loanApplicationRepository;

    public Flux<LoanApplication> execute(Integer page, Integer size) {
        return this.loanApplicationRepository.getPendingLoanApplications(page, size);
    }

}
