package co.com.crediya.model.loanapplication.gateways;

import co.com.crediya.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {
    Mono<String> saveLoanApplication(LoanApplication loanApplication);
}
