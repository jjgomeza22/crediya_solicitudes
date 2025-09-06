package co.com.crediya.r2dbc.mapper;

import co.com.crediya.r2dbc.repository.dto.LoanDetailsDto;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.BiFunction;

@Component
public class LoanDetailsRowMapper implements BiFunction<Row, RowMetadata, LoanDetailsDto> {

    @Override
    public LoanDetailsDto apply(Row row, RowMetadata rowMetadata) {
        return new LoanDetailsDto(
                row.get("id", Integer.class),
                row.get("amount", BigDecimal.class),
                row.get("time_limit", Integer.class),
                row.get("email", String.class),
                row.get("state", String.class),
                row.get("loan_name", String.class),
                row.get("interest_rate", BigDecimal.class)
        );
    }
}