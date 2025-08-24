package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.sendapplicationloan.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SendLoanApplicationUseCase implements IUseCaseMono<LoanApplication, String> {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;

    @Override
    public Mono<String> execute(LoanApplication request) {
        var loanTypeId = request.getLoanTypeId();
        return loanTypeRepository.findById(loanTypeId)
                .flatMap(lt -> loanApplicationRepository.saveLoanApplication(request))
                .switchIfEmpty(ApplicationExceptions.loanTypeNotFound());
    }
}
