package co.com.crediya.r2dbc.repository.dto;

import java.math.BigDecimal;

public record LoanDetailsDto(
        Integer loanId,
        BigDecimal loanAmount,
        Integer timeToLimit,
        String email,
        String state,
        String loanName,
        BigDecimal interestRate
) {

}
