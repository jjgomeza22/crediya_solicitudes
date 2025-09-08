package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.gateways.AuthenticationGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.exception.UserNotFoundException;
import co.com.crediya.usecase.sendapplicationloan.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
public class SendLoanApplicationUseCase implements IUseCaseMono<LoanApplication, String> {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final SQSSenderGateway sqsSenderGateway;
    private final AuthenticationGateway authenticationGateway;

    @Override
    public Mono<String> execute(LoanApplication request) {
        var loanTypeId = request.getLoanTypeId();
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(ApplicationExceptions.loanTypeNotFound())
                .flatMap(lt -> loanApplicationRepository.saveLoanApplication(request)
                        .zipWith(validateIfLoanTypeHasAutomaticValidation(lt.getAutomaticValidation(), request))
                        .map(Tuple2::getT1)
                );
    }

    private Mono<String> validateIfLoanTypeHasAutomaticValidation(Boolean hasAutomaticValidation, LoanApplication loanApplication) {
        if (Boolean.TRUE.equals(hasAutomaticValidation)) {
            return getUserData(loanApplication.getEmail())
                    .flatMap(user -> buildSqsMessage(user.name(), user.baseSalary()))
                    .flatMap(sqsSenderGateway::sendDebtCapacityQueue);
        }
        return Mono.just("OK");
    }

    private Mono<UserByEmailDto> getUserData(String email) {
        return authenticationGateway.getUsersInformation(email)
                .map(users -> Optional
                        .ofNullable(users.get(0))
                        .orElseThrow(() -> new UserNotFoundException(email))
                );
    }

    private Mono<String> buildSqsMessage(String name, BigDecimal baseSalary) {
        return Mono.just(String.format(
                "{\"name\": \"%s\", \"baseSalary\": \"%f\", \"totalDebt\": \"%f\"}",
                name,
                baseSalary,
                0.0
        ));
    }
}
