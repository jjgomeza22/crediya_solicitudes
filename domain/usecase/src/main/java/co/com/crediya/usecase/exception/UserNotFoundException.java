package co.com.crediya.usecase.exception;

public class UserNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Email [%s] not found";

    public UserNotFoundException(String email) {
        super(MESSAGE.formatted(email));
    }
}
