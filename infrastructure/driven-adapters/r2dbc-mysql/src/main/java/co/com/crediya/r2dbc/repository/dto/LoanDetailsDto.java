package co.com.crediya.r2dbc.repository.dto;

import java.math.BigDecimal;

public record LoanDetailsDto(
        Integer id,
        BigDecimal amount,
        Integer timeLimit,
        String email,
        String state,
        String loanName,
        BigDecimal interestRate
) {

}
