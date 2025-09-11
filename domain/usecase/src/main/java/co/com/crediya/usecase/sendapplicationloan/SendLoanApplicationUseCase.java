package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.loandetails.gateways.AuthenticationGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.IUseCaseMono;
import co.com.crediya.usecase.exception.UserNotFoundException;
import co.com.crediya.usecase.gettotaldebt.GetTotalDebtUseCase;
import co.com.crediya.usecase.sendapplicationloan.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SendLoanApplicationUseCase implements IUseCaseMono<LoanApplication, String> {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final SQSSenderGateway sqsSenderGateway;
    private final AuthenticationGateway authenticationGateway;
    private final GetTotalDebtUseCase getTotalDebtUseCase;

    @Override
    public Mono<String> execute(LoanApplication request) {
        var loanTypeId = request.getLoanTypeId();
        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(ApplicationExceptions.loanTypeNotFound())
                .flatMap(lt -> loanApplicationRepository.saveLoanApplication(request)
                        .flatMap(loan -> validateIfLoanTypeHasAutomaticValidation(lt, loan))
                );
    }

    private Mono<String> validateIfLoanTypeHasAutomaticValidation(LoanType loanType, LoanApplication loanApplication) {
        if (Boolean.TRUE.equals(loanType.getAutomaticValidation())) {
            return getUserData(loanApplication.getEmail())
                    .zipWith(loanApplicationRepository.finLoanApplicationsByStateAndEmail(List.of(StatesEnum.APPROVED.getStateId()), loanApplication.getEmail())
                            .collectList()
                    )
                    .flatMap(tuple -> this.buildSqsMessage(tuple, loanType, loanApplication))
                    .flatMap(sqsSenderGateway::sendDebtCapacityQueue)
                    .thenReturn("OK");
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

    private Mono<String> buildSqsMessage(Tuple2<UserByEmailDto, List<LoanDetails>> tuple, LoanType loanType, LoanApplication loanApplication) {
        var user = tuple.getT1();
        var totalDebt = getTotalDebtUseCase.execute(tuple.getT2());
        var amount = loanApplication.getAmount();
        var interestRate = loanType.getInterestRate();
        var timeLimit = loanApplication.getTimeLimit();
        var applicationId = loanApplication.getId();

        String jsonTemplate = "{\"name\":\"%s\",\"baseSalary\":\"%s\",\"totalDebt\":\"%s\",\"amount\":\"%s\",\"interestRate\":\"%s\",\"timeLimit\":\"%d\", \"applicationId\":\"%d\"}";

        return Mono.just(String.format(
                jsonTemplate,
                user.name(),
                user.baseSalary().toString(),
                totalDebt.toString(),
                amount.toString(),
                interestRate.toString(),
                timeLimit,
                applicationId
        ));
    }
}
