package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class LoanApplicationToReviewUseCase {
    private final LoanApplicationRepository loanApplicationRepository;

    public Flux<LoanDetails> execute(Integer page, Integer size) {
        return this.loanApplicationRepository.getPendingLoanApplications(page, size);
    }

}
