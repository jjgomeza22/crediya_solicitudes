package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.usecase.sendapplicationloan.exception.LoanTypeNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class SendLoanApplicationUseCaseTest {
    @InjectMocks
    SendLoanApplicationUseCase sendLoanApplicationUseCase;

    @Mock
    LoanApplicationRepository loanApplicationRepository;
    @Mock
    LoanTypeRepository loanTypeRepository;

    @Test
    void shouldSendLoanApplication() {
        var request = LoanApplication.builder()
                .amount(new BigDecimal(1500000))
                .timeLimit(12)
                .email("juan@mail.com")
                .stateId(4)
                .loanTypeId(1)
                .build();

        var loanType = LoanType.builder()
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(false)
                .build();

        Mockito.when(loanTypeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(loanType));
        Mockito.when(loanApplicationRepository.saveLoanApplication(Mockito.any(LoanApplication.class))).thenReturn(Mono.just("OK"));

        sendLoanApplicationUseCase.execute(request)
                .as(StepVerifier::create)
                .assertNext(res -> Assertions.assertEquals("OK", res))
                .verifyComplete();
    }

    @Test
    void shouldReturnLoanTypeNotFoundException() {
        var request = LoanApplication.builder()
                .amount(new BigDecimal(1500000))
                .timeLimit(12)
                .email("juan@mail.com")
                .stateId(4)
                .loanTypeId(1)
                .build();

        Mockito.when(loanTypeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

        sendLoanApplicationUseCase.execute(request)
                .as(StepVerifier::create)
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }
}