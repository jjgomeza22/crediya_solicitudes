package co.com.crediya.usecase.sendapplicationloan.exception;

public class LoanTypeNotFoundException extends RuntimeException {
    public LoanTypeNotFoundException(String message) {
        super(message);
    }
}
