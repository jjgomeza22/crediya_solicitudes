package co.com.crediya.model.loanstoreview;

import co.com.crediya.model.loandetails.LoanDetails;

import java.util.List;

public record LoanToReviewResponse(
        String name,
        List<LoanDetails> loanDetails
) {
}
