package co.com.crediya.usecase.gettotaldebt;

import co.com.crediya.model.loandetails.LoanDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GetTotalDebtUseCaseTest {
    @InjectMocks
    GetTotalDebtUseCase getTotalDebtUseCase;

    @Test
    void shouldGetTotalDebt() {
        var loanDetail = new LoanDetails(
                1,
                new BigDecimal(1000000),
                48,
                "juan@mail.com",
                "APROBADO",
                "Crédito de consumo",
                new BigDecimal("0.0178000")
        );

        var totalDebt = getTotalDebtUseCase.execute(List.of(loanDetail));
        Assertions.assertNotNull(totalDebt);
        Assertions.assertEquals(new BigDecimal("31159.65"), totalDebt);
    }

}