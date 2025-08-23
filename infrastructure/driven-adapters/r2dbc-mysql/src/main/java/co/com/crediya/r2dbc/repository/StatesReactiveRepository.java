package co.com.crediya.r2dbc.repository;

import co.com.crediya.r2dbc.entity.StatesEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StatesReactiveRepository extends ReactiveCrudRepository<StatesEntity, Integer>, ReactiveQueryByExampleExecutor<StatesEntity> {

}
