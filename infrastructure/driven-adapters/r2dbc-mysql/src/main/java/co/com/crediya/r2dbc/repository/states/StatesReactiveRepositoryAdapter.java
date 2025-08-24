package co.com.crediya.r2dbc.repository.states;

import co.com.crediya.model.states.States;
import co.com.crediya.model.states.gateways.StatesRepository;
import co.com.crediya.r2dbc.entity.StatesEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StatesReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        States,
        StatesEntity,
        Integer,
        StatesReactiveRepository
        > implements StatesRepository {
    public StatesReactiveRepositoryAdapter(StatesReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, States.class));
    }

    @Override
    public Mono<States> findById(Integer id) {
        return super.findById(id);
    }
}
