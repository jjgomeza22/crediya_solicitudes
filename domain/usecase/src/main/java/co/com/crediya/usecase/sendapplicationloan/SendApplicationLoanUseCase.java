package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.usecase.IUseCaseMono;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendApplicationLoanUseCase implements IUseCaseMono<LoanApplication, String> {

    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public Mono<String> execute(LoanApplication request) {
        return loanApplicationRepository.saveLoanApplication(request);
    }
}
