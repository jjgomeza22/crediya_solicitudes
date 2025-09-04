package co.com.crediya.r2dbc.repository.loantype;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.utils.constants.Method;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Integer,
        LoanTypeReactiveRepository
        > implements LoanTypeRepository {
    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    public Mono<LoanType> findById(int id) {
        var method = Method.FIND_STATUS_BY_ID;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return super.findById(id)
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }
}
