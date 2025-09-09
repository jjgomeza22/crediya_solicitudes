package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
import co.com.crediya.r2dbc.repository.dto.LoanDetailsDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Integer>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    @Query("""
            SELECT
                  s.id_solicitud AS loan_id,
                  s.monto AS loan_amount,
                  s.plazo AS time_to_limit,
                  s.email,
                  e.nombre AS state,
                  tp.nombre AS loan_name,
                  tp.tasa_interes AS interest_rate
            FROM
              solicitud s
            INNER JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo
            INNER JOIN estados e ON s.id_estado = e.id_estado
            WHERE
              s.id_estado IN (:stateIds) AND s.email = :email
            ORDER BY s.id_solicitud ASC
            """)
    Flux<LoanDetailsDto> findLoanDetailsByStateIdInAndUser(List<Integer> stateIds, String email);


    @Query("""
            SELECT
                  s.id_solicitud AS loan_id,
                  s.monto AS loan_amount,
                  s.plazo AS time_to_limit,
                  s.email,
                  e.nombre AS state,
                  tp.nombre AS loan_name,
                  tp.tasa_interes AS interest_rate
            FROM
              solicitud s
            INNER JOIN tipo_prestamo tp ON s.id_tipo_prestamo = tp.id_tipo_prestamo
            INNER JOIN estados e ON s.id_estado = e.id_estado
            WHERE
              s.id_estado IN (:stateIds)
            ORDER BY s.id_solicitud ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<LoanDetailsDto> findLoanDetailsByStateIdIn(List<Integer> stateIds, Integer limit, Integer offset);
}
