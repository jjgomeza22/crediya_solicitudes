package co.com.crediya.r2dbc.repository.loantype;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class LoanTypeReactiveRepositoryAdapterTest {
    @InjectMocks
    LoanTypeReactiveRepositoryAdapter loanTypeReactiveRepositoryAdapter;

    @Mock
    LoanTypeReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void shouldFindByIdOneLoanType() {
        var loanTypeEntity = LoanTypeEntity.builder()
                .id(1)
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(false)
                .build();

        var loanType = LoanType.builder()
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(false)
                .build();

        Mockito.when(mapper.map(loanTypeEntity, LoanType.class)).thenReturn(loanType);
        Mockito.when(repository.findById(1)).thenReturn(Mono.just(loanTypeEntity));

        loanTypeReactiveRepositoryAdapter.findById(1)
                .as(StepVerifier::create)
                .expectNextMatches(lt -> loanTypeEntity.getId() == 1)
                .verifyComplete();
    }
}