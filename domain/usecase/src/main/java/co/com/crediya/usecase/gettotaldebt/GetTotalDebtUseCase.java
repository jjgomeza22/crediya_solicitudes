package co.com.crediya.usecase.gettotaldebt;

import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.states.StatesEnum;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
public class GetTotalDebtUseCase {
    public BigDecimal execute(List<LoanDetails> loanDetails) {
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
