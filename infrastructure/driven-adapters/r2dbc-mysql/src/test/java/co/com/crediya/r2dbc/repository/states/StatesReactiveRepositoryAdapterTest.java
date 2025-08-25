package co.com.crediya.r2dbc.repository.states;

import co.com.crediya.model.states.States;
import co.com.crediya.r2dbc.entity.StatesEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StatesReactiveRepositoryAdapterTest {
    @InjectMocks
    StatesReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    StatesReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final StatesEntity statesEntity = StatesEntity.builder()
            .id(1)
            .name("name")
            .description("description")
            .build();

    private final States states = States.builder()
            .name("name")
            .description("description")
            .build();

    @Test
    void mustFindValueById() {

        when(repository.findById(1)).thenReturn(Mono.just(statesEntity));
        when(mapper.map(statesEntity, States.class)).thenReturn(states);

        Mono<States> result = repositoryAdapter.findById(1);

        StepVerifier.create(result).expectNextMatches(value -> value.getName().equals("name")).verifyComplete();
    }

}
