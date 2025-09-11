package co.com.crediya.usecase.sendapplicationloan;

import co.com.crediya.model.loanapplication.LoanApplication;
import co.com.crediya.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.crediya.model.loandetails.LoanDetails;
import co.com.crediya.model.loandetails.gateways.AuthenticationGateway;
import co.com.crediya.model.loandetails.gateways.dto.UserByEmailDto;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.usecase.gettotaldebt.GetTotalDebtUseCase;
import co.com.crediya.usecase.sendapplicationloan.exception.LoanTypeNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SendLoanApplicationUseCaseTest {
    @InjectMocks
    SendLoanApplicationUseCase sendLoanApplicationUseCase;

    @Mock
    LoanApplicationRepository loanApplicationRepository;
    @Mock
    LoanTypeRepository loanTypeRepository;
    @Mock
    SQSSenderGateway sqsSenderGateway;
    @Mock
    AuthenticationGateway authenticationGateway;
    @Mock
    GetTotalDebtUseCase getTotalDebtUseCase;

    private static LoanApplication loanApplication = LoanApplication.builder()
            .amount(new BigDecimal(1500000))
            .timeLimit(12)
            .email("juan@mail.com")
            .stateId(4)
            .loanTypeId(1)
            .build();

    @Test
    void shouldSendLoanApplication() {
        var loanType = LoanType.builder()
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(false)
                .build();

        Mockito.when(loanTypeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(loanType));
        Mockito.when(loanApplicationRepository.saveLoanApplication(Mockito.any(LoanApplication.class))).thenReturn(Mono.just(loanApplication));
        sendLoanApplicationUseCase.execute(loanApplication)
                .as(StepVerifier::create)
                .assertNext(res -> Assertions.assertEquals("OK", res))
                .verifyComplete();
    }

    @Test
    void shouldSendLoanApplicationWithAutomaticValidation() {
        var loanType = LoanType.builder()
                .name("libre inversión")
                .minAmount(new BigDecimal(500000))
                .maxAmount(new BigDecimal(10000000))
                .interestRate(new BigDecimal(2))
                .automaticValidation(true)
                .build();

        var user = new UserByEmailDto(
                "juan",
                "gomez",
                "juan@mail.com",
                new BigDecimal(8500000)
        );

        var loanDetail = new LoanDetails(
                1,
                new BigDecimal(1000000),
                48,
                "juan@mail.com",
                "APROBADO",
                "Crédito de consumo",
                new BigDecimal("0.0178000")
        );

        Mockito.when(loanTypeRepository.findById(Mockito.anyInt())).thenReturn(Mono.just(loanType));
        Mockito.when(loanApplicationRepository.saveLoanApplication(Mockito.any(LoanApplication.class))).thenReturn(Mono.just(loanApplication));
        Mockito.when(loanApplicationRepository.finLoanApplicationsByStateAndEmail(Mockito.any(List.class), Mockito.anyString())).thenReturn(Flux.just(loanDetail));
        Mockito.when(sqsSenderGateway.sendDebtCapacityQueue(Mockito.anyString())).thenReturn(Mono.just("sajfnd4-sdfdsf3"));
        Mockito.when(authenticationGateway.getUsersInformation(Mockito.anyString())).thenReturn(Mono.just(List.of(user)));
        Mockito.when(getTotalDebtUseCase.execute(Mockito.any(List.class))).thenReturn(BigDecimal.ZERO);

        sendLoanApplicationUseCase.execute(loanApplication)
                .as(StepVerifier::create)
                .assertNext(res -> Assertions.assertEquals("OK", res))
                .verifyComplete();
    }

    @Test
    void shouldReturnLoanTypeNotFoundException() {
        Mockito.when(loanTypeRepository.findById(Mockito.anyInt())).thenReturn(Mono.empty());

        sendLoanApplicationUseCase.execute(loanApplication)
                .as(StepVerifier::create)
                .expectError(LoanTypeNotFoundException.class)
                .verify();
    }
}