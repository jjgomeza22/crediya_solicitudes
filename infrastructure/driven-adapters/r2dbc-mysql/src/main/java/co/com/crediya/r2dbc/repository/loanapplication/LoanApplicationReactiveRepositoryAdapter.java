package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanDetailsRowMapper;
import co.com.crediya.r2dbc.repository.dto.LoanDetailsDto;
import co.com.crediya.utils.constants.Method;
import co.com.crediya.utils.constants.StatusResponse;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
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

    private final DatabaseClient databaseClient;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper, DatabaseClient databaseClient) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<String> saveLoanApplication(LoanApplication loanApplication) {
        var method = Method.SAVE_LOAN_APPLICATION;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return repository.save(toData(loanApplication))
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)))
                .then(Mono.just(StatusResponse.OK.getValue()));
    }

    @Override
    public Flux<LoanDetails> getPendingLoanApplications(Integer limit, Integer offset, List<Integer> stateIds) {
        var method = Method.GET_PENDING_LOAN_APPLICATIONS;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return this.getLoanDetails(stateIds, limit, offset)
                .map(d -> new LoanDetails(
                        d.amount(),
                        d.timeLimit(),
                        d.email(),
                        d.state(),
                        d.loanName(),
                        d.interestRate()
                ))
                .doOnComplete(() -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)));
    }

    public Flux<LoanDetailsDto> getLoanDetails(List<Integer> stateIds, Integer limit, Integer offset) {
        return databaseClient.sql(
                        """
                                SELECT
                                      tp.nombre AS loan_name,
                                      tp.tasa_interes AS interest_rate,
                                      s.monto AS amount,
                                      s.plazo AS time_limit,
                                      s.email,
                                      e.nombre AS state
                                FROM
                                  solicitud s
                                INNER JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo
                                INNER JOIN estados e ON s.id_estado = e.id_estado
                                WHERE
                                  s.id_estado IN (:stateIds)
                                ORDER BY s.id_solicitud ASC
                                LIMIT :limit OFFSET :offset
                                """
                )
                .bind("stateIds", stateIds)
                .bind("limit", limit)
                .bind("offset", offset)
                .map(new LoanDetailsRowMapper())
                .all();
    }
}