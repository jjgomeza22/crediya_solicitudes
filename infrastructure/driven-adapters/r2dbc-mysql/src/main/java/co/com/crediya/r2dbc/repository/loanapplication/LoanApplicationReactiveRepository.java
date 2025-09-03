package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Integer>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    Flux<LoanApplicationEntity> findByStateIdIn(List<Integer> ids, Pageable pageable);
}
