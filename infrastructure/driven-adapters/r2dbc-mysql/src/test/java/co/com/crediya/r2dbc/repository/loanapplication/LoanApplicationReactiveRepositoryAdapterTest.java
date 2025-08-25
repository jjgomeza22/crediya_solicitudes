package co.com.crediya.r2dbc.repository.loanapplication;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.r2dbc.entity.LoanApplicationEntity;
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
class LoanApplicationReactiveRepositoryAdapterTest {
    @InjectMocks
    LoanApplicationReactiveRepositoryAdapter loanApplicationReactiveRepositoryAdapter;

    @Mock
    LoanApplicationReactiveRepository loanApplicationReactiveRepository;

    @Mock
    ObjectMapper mapper;

    private final LoanApplicationEntity loanApplicationEntity = LoanApplicationEntity.builder()
            .amount(new BigDecimal(1500000))
            .timeLimit(12)
            .email("juan@mail.com")
            .stateId(4)
            .loanTypeId(1)
            .build();

    private final LoanApplication loanApplication = LoanApplication.builder()
            .amount(new BigDecimal(1500000))
            .timeLimit(12)
            .email("juan@mail.com")
            .stateId(4)
            .loanTypeId(1)
            .build();

    @Test
    void shouldSendLoanApplication() {
        Mockito.when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(loanApplicationEntity);

        Mockito.when(loanApplicationReactiveRepository.save(loanApplicationEntity)).thenReturn(Mono.just(loanApplicationEntity));

        loanApplicationReactiveRepositoryAdapter.saveLoanApplication(loanApplication)
                .as(StepVerifier::create)
                .expectNext("OK")
                .verifyComplete();
    }
}