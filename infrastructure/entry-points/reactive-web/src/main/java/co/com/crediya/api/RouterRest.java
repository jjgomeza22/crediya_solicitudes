package co.com.crediya.api;

import co.com.crediya.model.loanapplication.LoanApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final LoanApplicationHandler handler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = "sendApplicationLoan",
                    operation = @Operation(
                            operationId = "sendApplicationLoan",
                            summary = "Send new loan application",
                            description = "Send new loan application with input data.",
                            tags = {"Loan Application"},
                            requestBody = @RequestBody(
                                    content = @Content(
                                            schema = @Schema(implementation = LoanApplication.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "OK: loan application has been created successful",
                                            content = @Content(
                                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                                    schema = @Schema(implementation = String.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Not-Found: Loan type not found"
                                    ),
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction() {
        return route()
                .GET("/", req -> ServerResponse.permanentRedirect(URI.create("/swagger-ui.html")).build())
                .POST("/api/v1/solicitud", handler::sendApplicationLoan)
                .build();
    }
}
