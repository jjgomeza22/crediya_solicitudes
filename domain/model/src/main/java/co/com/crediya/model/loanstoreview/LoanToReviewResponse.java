package co.com.crediya.model.loanstoreview;

import co.com.crediya.model.loandetails.LoanDetails;

import java.math.BigDecimal;
import java.util.List;

public record LoanToReviewResponse(
        String name,
        String email,
        BigDecimal baseSalary,
        BigDecimal totalMonthlyDebt,
        List<LoanDetails> loanDetails
) {
}
