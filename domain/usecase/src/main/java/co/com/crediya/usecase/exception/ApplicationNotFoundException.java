package co.com.crediya.usecase.exception;

public class ApplicationNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Application [id=&d] is not found";
    public ApplicationNotFoundException(Integer id) {
        super(MESSAGE.formatted(id));
    }
}
