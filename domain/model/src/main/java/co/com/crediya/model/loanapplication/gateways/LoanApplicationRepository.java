package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loandetails.LoanDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationRepository {
    Mono<String> saveLoanApplication(LoanApplication loanApplication);

    Flux<LoanDetails> getPendingLoanApplications(Integer page, Integer size, List<Integer> stateIds);

    Mono<LoanApplication> findApplicationById(Integer id);
}
