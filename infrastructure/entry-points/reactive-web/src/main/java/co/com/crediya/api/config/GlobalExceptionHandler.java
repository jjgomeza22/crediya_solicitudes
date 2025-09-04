package co.com.crediya.api.config;

import co.com.crediya.security.exception.InvalidAuthException;
import co.com.crediya.usecase.sendapplicationloan.exception.InvalidInputException;
import co.com.crediya.usecase.sendapplicationloan.exception.LoanTypeNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Component
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    @Value("${docs.user-handler.post-users}")
    private String postUserDocumentation;

    private final BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> handleInvalidInput =
            (ex, req) -> handleException((InvalidInputException) ex, req);

    private final BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> handleLoanType =
            (ex, req) -> handleException((LoanTypeNotFoundException) ex, req);

    private final BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> handleInvalidAuth =
            (ex, req) -> handleException((InvalidAuthException) ex, req);

    private final Map<Class<? extends Throwable>, BiFunction<Throwable, ServerRequest, Mono<ServerResponse>>> EXCEPTION_HANDLERS = Map.of(
            InvalidInputException.class, handleInvalidInput,
            InvalidAuthException.class, handleInvalidAuth,
            LoanTypeNotFoundException.class, handleLoanType
    );

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageReaders(codecConfigurer.getReaders());
        this.setMessageWriters(codecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        BiFunction<Throwable, ServerRequest, Mono<ServerResponse>> handler = EXCEPTION_HANDLERS.get(error.getClass());

        if (handler != null) {
            return handler.apply(error, request);
        }
        return handleGenericException(request);
    }

    private Mono<ServerResponse> handleGenericException(ServerRequest request) {
        Map<String, Object> errorMap = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorMap);
    }

    private Mono<ServerResponse> handleException(InvalidAuthException ex, ServerRequest request) {
        return handleException(HttpStatus.UNAUTHORIZED, ex, request, problemDetail -> problemDetail.setTitle("Invalid auth"));
    }

    public Mono<ServerResponse> handleException(LoanTypeNotFoundException ex, ServerRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> problemDetail.setTitle("You entered a loan type that does not exist."));
    }

    public Mono<ServerResponse> handleException(InvalidInputException ex, ServerRequest request) {
        return handleException(HttpStatus.BAD_REQUEST, ex, request, problemDetail -> {
            problemDetail.setTitle("Invalid Input");
            problemDetail.setType(URI.create(postUserDocumentation));
        });
    }

    private Mono<ServerResponse> handleException(HttpStatus status, Exception ex, ServerRequest request, Consumer<ProblemDetail> problem) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setInstance(URI.create(request.path()));
        problem.accept(problemDetail);
        return ServerResponse.status(status).bodyValue(problemDetail);
    }
}