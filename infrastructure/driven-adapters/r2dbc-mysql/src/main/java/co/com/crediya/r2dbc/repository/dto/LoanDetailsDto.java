package co.com.crediya.r2dbc.repository.dto;

import java.math.BigDecimal;

public record LoanDetailsDto(
        BigDecimal amount,
        Integer timeLimit,
        String email,
        Integer stateId,
        String loanName,
        BigDecimal interestRate
) {

}
