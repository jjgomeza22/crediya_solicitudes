package co.com.crediya.api.dto;

import java.math.BigDecimal;

public record SendLoanApplicationDto(
        BigDecimal amount,
        int timeLimit,
        String email,
        int loanTypeId
) {
}
