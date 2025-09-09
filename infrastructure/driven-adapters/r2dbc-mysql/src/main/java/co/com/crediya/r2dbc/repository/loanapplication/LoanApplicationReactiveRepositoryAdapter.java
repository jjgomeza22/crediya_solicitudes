package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanDetailsMapper;
import co.com.crediya.utils.constants.Method;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Integer,
        LoanApplicationReactiveRepository
        > implements LoanApplicationRepository {

    private final LoanDetailsMapper loanDetailsMapper;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper, LoanDetailsMapper loanDetailsMapper) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.loanDetailsMapper = loanDetailsMapper;
    }

    @Override
    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        var method = Method.SAVE_LOAN_APPLICATION;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return repository.save(toData(loanApplication))
                .map(super::toEntity)
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }

    @Override
    public Flux<LoanDetails> getPendingLoanApplications(Integer limit, Integer offset, List<Integer> stateIds) {
        var method = Method.GET_PENDING_LOAN_APPLICATIONS;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return repository.findLoanDetailsByStateIdIn(stateIds, limit, offset)
                .map(loanDetailsMapper::toModel)
                .doOnComplete(() -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }

    @Override
    public Flux<LoanDetails> finLoanApplicationsByStateAndEmail(List<Integer> stateIds, String email) {
        return repository.findLoanDetailsByStateIdInAndUser(stateIds, email)
                .map(loanDetailsMapper::toModel);
    }

    @Override
    public Mono<LoanApplication> findApplicationById(Integer id) {
        var method = Method.FIND_APPLICATION_BY_ID;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return super.findById(id)
                .doOnNext(data -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }
}