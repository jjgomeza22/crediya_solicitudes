package co.com.crediya.model.loandetails.gateways;

import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthenticationGateway {
    Mono<List<UserByEmailDto>> getUsersInformation(String emails);
}
