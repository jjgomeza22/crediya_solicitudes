package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Integer,
        LoanApplicationReactiveRepository
        > implements LoanApplicationRepository {
    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }

    @Override
    public Mono<String> saveLoanApplication(LoanApplication loanApplication) {
        var method = "saveLoanApplication";
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return repository.save(toData(loanApplication))
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)))
                .then(Mono.just("OK"));
    }

    @Override
    public Flux<LoanApplication> getPendingLoanApplications(Integer page, Integer size) {
        var stateIds = Stream.of(
                StatesEnum.DECLINE.getStateId(),
                StatesEnum.PRE_APPROVED.getStateId(),
                StatesEnum.PE_REVIEW.getStateId()
        ).toList();

        return repository.findByStateIdIn(stateIds, PageRequest.of(page - 1, size))
                .map(super::toEntity);
    }
}