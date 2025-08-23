package co.com.crediya.model.states.gateways;

import co.com.crediya.model.states.States;
import reactor.core.publisher.Mono;

public interface StatesRepository {
    Mono<States> findById(Integer id);
}
