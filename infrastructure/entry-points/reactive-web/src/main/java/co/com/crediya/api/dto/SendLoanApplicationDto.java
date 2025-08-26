package co.com.crediya.api.dto;

import java.math.BigDecimal;

public record SendLoanApplicationDto(
        BigDecimal amount,
        Integer timeLimit,
        String email,
        Integer loanTypeId
) {
}
