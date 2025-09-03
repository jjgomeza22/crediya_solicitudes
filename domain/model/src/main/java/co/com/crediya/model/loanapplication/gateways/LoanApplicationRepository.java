package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<String> saveLoanApplication(LoanApplication loanApplication);
    Flux<LoanApplication> getPendingLoanApplications(Integer page, Integer size);
}
