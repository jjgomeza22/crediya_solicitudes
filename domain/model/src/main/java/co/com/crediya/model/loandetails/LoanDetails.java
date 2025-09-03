package co.com.crediya.model.loandetails;

import java.math.BigDecimal;

public record LoanDetails(
        BigDecimal amount,
        Integer timeLimit,
        String email,
        Integer stateId,
        String loanName,
        BigDecimal interestRate
) {

}
