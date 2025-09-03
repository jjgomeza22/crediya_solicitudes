package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.states.StatesEnum;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanDetailsRowMapper;
import co.com.crediya.r2dbc.repository.dto.LoanDetailsDto;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
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

    private final DatabaseClient databaseClient;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper, DatabaseClient databaseClient) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.databaseClient = databaseClient;
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
    public Flux<LoanDetails> getPendingLoanApplications(Integer page, Integer size) {
        var stateIds = Stream.of(
                StatesEnum.DECLINE.getStateId(),
                StatesEnum.PRE_APPROVED.getStateId(),
                StatesEnum.PE_REVIEW.getStateId()
        ).toList();

        return this.getLoanDetails()
                .map(d -> new LoanDetails(
                        d.amount(),
                        d.timeLimit(),
                        d.email(),
                        d.stateId(),
                        d.loanName(),
                        d.interestRate()
                ));
    }

    public Flux<LoanDetailsDto> getLoanDetails() {
        return databaseClient.sql(
                        """
                                SELECT
                                    s.monto AS amount,
                                    s.plazo AS time_limit,
                                    s.email,
                                    s.id_estado AS state_id,
                                    tp.nombre AS loan_name,
                                    tp.tasa_interes AS interest_rate
                                FROM
                                    solicitud s
                                INNER JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo
                                WHERE
                                    s.id_estado IN (2, 3, 4)
                                ORDER BY s.id_estado DESC
                                """
                )
                .map(new LoanDetailsRowMapper())
                .all();
    }
}