package co.com.crediya.api;

import co.com.crediya.api.config.ApplicationExceptionHandler;
import co.com.crediya.usecase.sendapplicationloan.exception.LoanTypeNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler handler, ApplicationExceptionHandler exceptionHandler) {
        return route()
                .POST("/api/v1/solicitud", handler::sendApplicationLoan)
                .onError(LoanTypeNotFoundException.class, exceptionHandler::handleException)
                .build();
    }
}
