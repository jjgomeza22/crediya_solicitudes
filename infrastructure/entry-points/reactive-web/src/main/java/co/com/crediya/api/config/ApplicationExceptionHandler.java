package co.com.crediya.api.config;

import co.com.crediya.usecase.sendapplicationloan.exception.LoanTypeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

@Service
public class ApplicationExceptionHandler {
    public Mono<ServerResponse> handleException(LoanTypeNotFoundException ex, ServerRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> problemDetail.setDetail("You entered a loan type that does not exist."));
    }

    private Mono<ServerResponse> handleException(HttpStatus status, Exception ex, ServerRequest request, Consumer<ProblemDetail> problem) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setInstance(URI.create(request.path()));
        problem.accept(problemDetail);
        return ServerResponse.status(status).bodyValue(problemDetail);
    }
}
