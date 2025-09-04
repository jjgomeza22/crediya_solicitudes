package co.com.crediya.usecase.loandaplicationtoreview;

import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.loandetails.gateways.UsersByEmailGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.model.loanstoreview.LoanToReviewResponse;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.usecase.sendapplicationloan.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoanApplicationToReviewUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final UsersByEmailGateway usersByEmailGateway;

    public Flux<LoanToReviewResponse> execute(Integer limit, Integer offset, List<String> states) {
        Mono<List<LoanDetails>> allLoanDetails = this.getLoanApplicationDetails(limit, offset, states).share();
        return this.buildLoanToReviewResponse(allLoanDetails);
    }

    private Mono<List<LoanDetails>> getLoanApplicationDetails(Integer limit, Integer offset, List<String> states) {
        return Flux.fromIterable(states)
                .flatMap(state -> Mono.just(StatesEnum.valueOf(state).getStateId()))
                .onErrorResume(ex -> Mono.error(new InvalidInputException("Invalid state")))
                .collectList()
                .flatMapMany(statesIdList -> this.loanApplicationRepository
                        .getPendingLoanApplications(limit, offset, statesIdList)
                )
                .collectList();
    }

    private Mono<Map<String, UserByEmailDto>> getUsersInformation(Mono<List<LoanDetails>> allLoanDetails) {
        return allLoanDetails
                .map(ld -> ld.stream()
                        .map(LoanDetails::email)
                        .distinct()
                        .collect(Collectors.joining(","))
                )
                .flatMap(usersByEmailGateway::getUsersInformation)
                .map(users -> users.stream()
                        .collect(Collectors.toMap(UserByEmailDto::email, user -> user))
                );
    }

    private Flux<LoanToReviewResponse> buildLoanToReviewResponse(Mono<List<LoanDetails>> allLoanDetails) {
        return allLoanDetails
                .zipWith(this.getUsersInformation(allLoanDetails))
                .flatMapMany(tuple -> {
                    List<LoanDetails> allDetails = tuple.getT1();
                    Map<String, UserByEmailDto> userMap = tuple.getT2();

                    return Flux.fromIterable(allDetails)
                            .groupBy(LoanDetails::email)
                            .flatMap(group -> group.collectList()
                                    .map(loans -> {
                                        UserByEmailDto user = userMap.get(group.key());
                                        return new LoanToReviewResponse(
                                                user.name(),
                                                user.email(),
                                                user.baseSalary(),
                                                getTotalMonthlyDebt(loans),
                                                loans
                                        );
                                    }));
                });
    }

    private BigDecimal getTotalMonthlyDebt(List<LoanDetails> loanDetails) {
        return loanDetails.stream()
                .filter(loan -> loan.state().equals(StatesEnum.APPROVED.getName()))
                .map(loan -> calculateMonthlyPayment(loan.amount(), loan.interestRate(), loan.timeLimit()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyInterestRate, int termInMonths) {
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(termInMonths), MathContext.DECIMAL128);
        }
        BigDecimal ratePlusOne = monthlyInterestRate.add(BigDecimal.ONE);
        BigDecimal numerator = monthlyInterestRate.multiply(ratePlusOne.pow(termInMonths));
        BigDecimal denominator = ratePlusOne.pow(termInMonths).subtract(BigDecimal.ONE);

        return principal.multiply(numerator.divide(denominator, MathContext.DECIMAL128))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
