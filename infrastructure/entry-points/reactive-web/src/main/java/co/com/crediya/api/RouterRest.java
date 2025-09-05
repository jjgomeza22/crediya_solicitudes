package co.com.crediya.api;

import co.com.crediya.api.config.ApplicationPath;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanstoreview.LoanToReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    private final ApplicationPath applicationPath;
    private final LoanApplicationHandler handler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = "loanApplicationToReview",
                    operation = @Operation(
                            operationId = "loanApplicationToReview",
                            summary = "Get loan applications for review",
                            description = "Retrieves a list of loan applications to be reviewed, based on filtering by state, with pagination.",
                            tags = {"Loan Application"},
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "limit",
                                            description = "Maximum number of results to return.",
                                            required = false,
                                            schema = @Schema(type = "integer", defaultValue = "1")
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "offset",
                                            description = "The number of items to skip before starting to collect the result set.",
                                            required = false,
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "states",
                                            description = "Loan application states to filter by. you could overcharge the state param to filter by one or more states => APPROVED, DECLINE, PRE_APPROVED, PE_REVIEW",
                                            required = false,
                                            schema = @Schema(type = "string", defaultValue = "PE_REVIEW")
                                    )
                            },
                            security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "BearerAuth"),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "OK: Loan applications retrieved successfully.",
                                            content = @Content(
                                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                    schema = @Schema(implementation = LoanToReviewResponse.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Unauthorized: Authentication required."
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Forbidden: User does not have the 'ADVISOR' authority."
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = "sendLoanApplication",
                    operation = @Operation(
                            operationId = "sendLoanApplication",
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
                .GET(applicationPath.getApplication(), handler::loanApplicationToReview)
                .POST(applicationPath.getApplication(), handler::sendLoanApplication)
                .build();
    }
}
